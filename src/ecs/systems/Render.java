package ecs.systems;

import ecs.EcsSystem;
import ecs.components.WindowInformation;
import render.PhongShader;
import ecs.components.*;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import render.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import linalib.flt.*;

public class Render extends EcsSystem {

    protected PhongShader shader = new PhongShader();

    private int currMesh = -1;
    private int currTexture = -1;

    private FVec3 background;

    public Render(FVec3 background) {
        this.background = background;
    }

    public ShaderProgram getShader() {
        return this.shader;
    }

    AmbientLight defaulAmbientLight = new AmbientLight(new FVec3(1));
    DirectionalLight defaulDirectionalLight = new DirectionalLight(new FVec3(1), new FVec3(1), 1);
    Fog defaultFog = new Fog(0, 1, new FVec3(0));
    Scale defaultScale = new Scale(new FVec3(1));
    Position defaultPosition = new Position(new FVec3(0));
    Orientation defaulOrientation = new Orientation(new FQuaternion().initRotation(0, FVec3.YAXIS));

    @Override
    public void init() {
        register("render", 9, this::onRender);
    }

    public void onRender() {

        store().read((readable) -> {
            WindowInformation windowInformation = readable.getComponent(0, WindowInformation.class);
            int windowWidth = windowInformation.getWidth();
            int windowHeight = windowInformation.getHeight();

            // prepare
            glEnable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor((float) background.x, (float) background.y, (float) background.z, 1.0f);

            int[] entitiesWithCameras = readable.filterEntities(Camera.class);
            if (entitiesWithCameras.length == 0) // if there is no camera do not continue
                return;
            int cameraEntity = entitiesWithCameras[0];

            Camera camera = readable.getComponent(cameraEntity, Camera.class);
            Position cameraPosition = readable.getComponent(cameraEntity, Position.class);
            if (cameraPosition == null)
                cameraPosition = defaultPosition;
            Orientation cameraOrientation = readable.getComponent(cameraEntity, Orientation.class);
            if (cameraOrientation == null)
                cameraOrientation = defaulOrientation;

            // Fog
            int[] fogEntities = readable.filterEntities(Fog.class);
            Fog fog = fogEntities.length > 0 ? readable.getComponent(fogEntities[0], Fog.class) : defaultFog;
            // Directional light
            int[] directionalLightEntities = readable.filterEntities(DirectionalLight.class);
            DirectionalLight directionalLight = directionalLightEntities.length > 0
                    ? readable.getComponent(directionalLightEntities[0], DirectionalLight.class)
                    : defaulDirectionalLight;
            // Ambient Light
            int[] ambientLightEntities = readable.filterEntities(AmbientLight.class);
            AmbientLight ambientLight = ambientLightEntities.length > 0
                    ? readable.getComponent(ambientLightEntities[0], AmbientLight.class)
                    : defaulAmbientLight;

            if (!getShader().isCreated)
                getShader().create();
            getShader().start();

            int posStoreAttrNum = 0;
            int texStoreAttrNum = 1;
            int normStoreAttrNum = 2;

            for (int entity : readable.filterEntities(MeshReference.class, TextureReference.class)) {

                MeshReference meshReference = readable.getComponent(entity, MeshReference.class);
                TextureReference texture = readable.getComponent(entity, TextureReference.class);
                Scale scale = readable.getComponent(entity, Scale.class);
                if (scale == null) {
                    scale = defaultScale;
                }
                Position position = readable.getComponent(entity, Position.class);
                if (position == null) {
                    position = defaultPosition;
                }
                Orientation orientation = readable.getComponent(entity, Orientation.class);
                if (orientation == null) {
                    orientation = defaulOrientation;
                }

                FMat4 translationMatrix = FMat4.newTranslation3(position.getVector()).transpose();
                FMat4 rotationMatrix = FMat4.newRot3FromQuaternion(orientation.getQuaternion());
                FMat4 scaleMatrix = FMat4.newScale3(scale.getVector());

                // local model space => world space
                FMat4 modelToWorldMatrix = new FMat4()
                        .mul(scaleMatrix)
                        .mul(rotationMatrix)
                        .mul(translationMatrix)
                ;

                // local model space => world space => view space => projection space
                FMat4 modelToProjectionMatrix = new FMat4()
                        // #1 model space to world space
                        .mul(modelToWorldMatrix)

                        // #2 world space to view space
                        .mulView3FromQuaternion(cameraPosition.getVector(), cameraOrientation.getQuaternion())
                        // #3 view space to projection space
                        .mulPerspective3Fov(windowWidth / (float) windowHeight, camera.getZNear(), camera.getZFar(),
                                camera.getFOV())

                ;

                FMat4 textureMatrix = new FMat4(1.0f / texture.getSizeOfAtlas().getX(), 0, 0, 0, 0,
                        1.0f / texture.getSizeOfAtlas().getY(), 0, 0,
                        texture.getPositionOnAtlas().getX() / texture.getSizeOfAtlas().getX(),
                        texture.getPositionOnAtlas().getY() / texture.getSizeOfAtlas().getY(), 0, 0, 0, 0, 0, 0);

                getShader().setUniform("worldMat", modelToWorldMatrix);
                getShader().setUniform("projectedMat", modelToProjectionMatrix);
                getShader().setUniform("cameraPosition", cameraPosition.getVector());
                getShader().setUniform("textureMat", textureMatrix);
                getShader().setUniform("textureRepeat", texture.getRepeatVecor());

                getShader().setUniform("fogColor", fog.getColor());
                getShader().setUniform("fogDensity", fog.getDensity());
                getShader().setUniform("fogGradient", fog.getGradient());

                getShader().setUniform("baseColor", new FVec3(1));
                getShader().setUniform("dye", texture.getDyeColor());
                getShader().setUniform("ambientLight", ambientLight.getColor());
                getShader().setUniform("directionalLight.base.color", directionalLight.getColor());
                getShader().setUniform("directionalLight.base.intensity", directionalLight.getIntensity());
                getShader().setUniform("directionalLight.direction", directionalLight.getDirection());

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
