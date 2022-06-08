package ecs.systems;

import ecs.Component;
import ecs.EcsSystem;
import render.PhongShader;
import ecs.components.*;
import linalib.Mat4;
import linalib.Quaternion;
import linalib.Vec3;
import linalib.Vec3Readable;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import render.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class Render extends EcsSystem {

    protected PhongShader shader = new PhongShader();

    private int currMesh = -1;
    private int currTexture = -1;

    private Vec3 background;

    public Render(Vec3 background) {
        this.background = background;
    }

    public ShaderProgram getShader() {
        return this.shader;
    }

    @Override
    public void init() {
        register("render", 9, this::onRender);
    }

    public void onRender() {

        store().read((readable) -> {
            WindowInformation windowInformation = readable.getComponent0(0, WindowInformation.class);
            int windowWidth = windowInformation.getWidth();
            int windowHeight = windowInformation.getHeight();

            // prepare
            glEnable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor((float) background.x, (float) background.y, (float) background.z, 1.0f);

            int[] entitiesWithCameras = readable.filterEntities0(Camera.class);
            if (entitiesWithCameras.length == 0) // if there is no camera do not continue
                return;
            int cameraEntity = entitiesWithCameras[0];

            Camera camera = readable.getComponent0(cameraEntity, Camera.class);
            Position cameraPosition = readable.getComponentOrStandard0(cameraEntity, Position.class);
            Orientation cameraOrientation = readable.getComponentOrStandard0(cameraEntity, Orientation.class);

            // Fog
            int[] fogEntities = readable.filterEntities0(Fog.class);
            Fog fog = fogEntities.length > 0 ? readable.getComponent0(fogEntities[0], Fog.class) : Fog.standard();
            
            // Directional light
            int[] lightPlaneComponents = readable.filterEntities0(LightPlane.class);
            LightPlane lightPlane;
            Vec3Readable lightPlaneForwardVector = null;
            if (lightPlaneComponents.length == 0) {
                lightPlane = LightPlane.standard();
                lightPlaneForwardVector = Vec3.FORWARD;
            } else {
                lightPlane = readable.getComponent0(lightPlaneComponents[0], LightPlane.class);
                lightPlaneForwardVector = new Vec3(Vec3.FORWARD).rotateByQuaternion(
                        readable.getComponentOrStandard0(lightPlaneComponents[0], Orientation.class).getQuaternion());
            }

            // Ambient Light
            int[] ambientLightEntities = readable.filterEntities0(AmbientLight.class);
            AmbientLight ambientLight = ambientLightEntities.length > 0
                    ? readable.getComponent0(ambientLightEntities[0], AmbientLight.class)
                    : AmbientLight.standard();

            if (!getShader().isCreated)
                getShader().create();
            getShader().start();

            int posStoreAttrNum = 0;
            int texStoreAttrNum = 1;
            int normStoreAttrNum = 2;

            for (int entity : readable.filterEntities0(MeshReference.class, TextureReference.class)) {

                MeshReference meshReference = readable.getComponent0(entity, MeshReference.class);
                TextureReference texture = readable.getComponent0(entity, TextureReference.class);
                Scale scale = readable.getComponentOrStandard0(entity, Scale.class);
                Position position = readable.getComponentOrStandard0(entity, Position.class);
                Orientation orientation = readable.getComponentOrStandard0(entity, Orientation.class);

                Mat4 translationMatrix = Mat4.initTranslation3(position.getVector()).transpose();
                Mat4 rotationMatrix = Mat4.initRot3FromQuaternion(orientation.getQuaternion()).transpose();
                Mat4 scaleMatrix = Mat4.initScale3(scale.getVector()).transpose();

                Mat4 modelToWorldMatrix = new Mat4(translationMatrix).premul((rotationMatrix).premul(scaleMatrix));

                // local model space => world space => view space => projection space
                Mat4 modelToProjectionMatrix = new Mat4()
                        // #3 view space to projection space
                        .mul(Mat4.initPerspective3FoV(windowWidth / (float) windowHeight, camera.getZNear(),
                                camera.getZFar(),
                                camera.getFOV()))
                        // #2 world space to view space
                        .mul(Mat4.initView3FromQuaternion(cameraPosition.getVector(),
                                new Quaternion(cameraOrientation.getQuaternion())))
                        // #1 model space to world space
                        .mul(Mat4.initTranslation3(position.getVector()))
                        .mul(Mat4.initRot3FromQuaternion(orientation.getQuaternion()))
                        .mul(Mat4.initScale3(scale.getVector()));

                Mat4 textureMatrix = new Mat4(1.0f / texture.getSizeOfAtlas().getX(), 0, 0, 0, 0,
                        1.0f / texture.getSizeOfAtlas().getY(), 0, 0,
                        texture.getPositionOnAtlas().getX() / texture.getSizeOfAtlas().getX(),
                        texture.getPositionOnAtlas().getY() / texture.getSizeOfAtlas().getY(), 0, 0, 0, 0, 0, 0);

                getShader().setUniform("MWMatrix", modelToWorldMatrix);
                getShader().setUniform("MWVPMatrix", modelToProjectionMatrix.transpose());
                getShader().setUniform("cameraPosition", cameraPosition.getVector());
                getShader().setUniform("textureMat", textureMatrix);
                getShader().setUniform("textureRepeat", texture.getRepeatVecor());

                getShader().setUniform("fogColor", fog.getColor());
                getShader().setUniform("fogDensity", fog.getDensity());
                getShader().setUniform("fogGradient", fog.getGradient());

                getShader().setUniform("baseColor", new Vec3(1));
                getShader().setUniform("dye", texture.getDyeColor());
                getShader().setUniform("ambientLight", ambientLight.getColor());
                getShader().setUniform("directionalLight.base.color", lightPlane.getColor());
                getShader().setUniform("directionalLight.base.intensity", lightPlane.getIntensity());
                getShader().setUniform("directionalLight.direction", lightPlaneForwardVector);

                // if a different mesh occurs load new one
                if (meshReference.getVAO() != currMesh) {
                    if (!(currMesh == -1)) {
                        // unload previous mesh
                        glDisableVertexAttribArray(MeshReference.POS_STORE_ATTR_NUM);
                        glDisableVertexAttribArray(MeshReference.TEX_STORE_ATTR_NUM);
                        glDisableVertexAttribArray(MeshReference.NORM_STORE_ATTR_NUM);
                        glBindVertexArray(0);
                    }
                    // load new mesh
                    glBindVertexArray(meshReference.getVAO());
                    GL20.glEnableVertexAttribArray(MeshReference.POS_STORE_ATTR_NUM);
                    GL20.glEnableVertexAttribArray(MeshReference.TEX_STORE_ATTR_NUM);
                    GL20.glEnableVertexAttribArray(MeshReference.NORM_STORE_ATTR_NUM);
                    currMesh = meshReference.getVAO();
                }

                // if a different texture occurs
                if (texture.getLocation() != currTexture) {
                    GL13.glActiveTexture(GL13.GL_TEXTURE0);
                    glBindTexture(GL_TEXTURE_2D, texture.getLocation());
                    currTexture = texture.getLocation();
                }

                // enable back face culling
                if (texture.hasTransparency())
                    glDisable(GL_CULL_FACE);
                else
                    glEnable(GL_CULL_FACE);

                glDrawElements(GL_TRIANGLES, meshReference.getNumberOfIndices(), GL_UNSIGNED_INT, 0);
            }

            // unload last mesh
            glDisableVertexAttribArray(posStoreAttrNum);
            glDisableVertexAttribArray(texStoreAttrNum);
            glDisableVertexAttribArray(normStoreAttrNum);
            glBindVertexArray(0);
            currMesh = -1;

            getShader().stop();

        });
    }

}
