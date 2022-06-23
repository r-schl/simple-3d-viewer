package ecs.components;

import render.BufferUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import ecs.Component;
import linalib.Vec3;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class MeshReference extends Component {

    private int VAO; // VAO: Vertex Attribute Array
    private int numberOfIndices;
    private int numberOfPositions;

    public static final int POS_STORE_ATTR_NUM = 0;
    public static final int TEX_STORE_ATTR_NUM = 1;
    public static final int NORM_STORE_ATTR_NUM = 2;

    public MeshReference(int VAO, int numberOfPositions, int numberOfIndices) {
        this.VAO = VAO;
        this.numberOfIndices = numberOfPositions;
        this.numberOfPositions = numberOfIndices;
    }

    public int getVAO() {
        return this.VAO;
    }

    public int getNumberOfIndices() {
        return this.numberOfIndices;
    }

    public int getnumberOfPositions() {
        return this.numberOfPositions;
    }


    public static MeshReference load(float[] positions, float[] texCoordinates, float[] normals, int[] indices) {
        // create buffers
        FloatBuffer buffPosition = BufferUtils.flippedFloat(positions);
        FloatBuffer buffTexCoordinates = BufferUtils.flippedFloat(texCoordinates);
        FloatBuffer buffNormals = BufferUtils.flippedFloat(normals);
        IntBuffer buffIndices = BufferUtils.flippedInt(indices);

        int VAO = GL30.glGenVertexArrays(); // create VAO
        glBindVertexArray(VAO); // open VAO
        int VBOPositions = storeBufferInAttrList(POS_STORE_ATTR_NUM, 3, buffPosition);
        int VBOTexCoordinates = storeBufferInAttrList(TEX_STORE_ATTR_NUM, 2, buffTexCoordinates);
        int VBONormals = storeBufferInAttrList(NORM_STORE_ATTR_NUM, 3, buffNormals);

        // store indices in VAO
        int VBOIndices = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VBOIndices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffIndices, GL15.GL_STATIC_DRAW);
        glBindVertexArray(0); // close VAO

        MeshReference meshReference = new MeshReference(VAO, positions.length, indices.length);
        return meshReference;
    }

    public static MeshReference load(float[] positions, float[] texCoordinates, int[] indices) {
        return MeshReference.load(positions, texCoordinates, calculateNormals(positions, indices), indices);
    }

    private synchronized static int storeBufferInAttrList(int attrNumber, int size, FloatBuffer buffer) {
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL_ARRAY_BUFFER, vbo); // open vbo
        GL15.glBufferData(GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // store data
        GL20.glVertexAttribPointer(attrNumber, size, GL11.GL_FLOAT, false, 0, 0); // create pointer
        GL15.glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    private static float[] calculateNormals(float[] positions, int[] indices) {
        float[] normals = new float[positions.length];
        // calculate Normals
        for (int i = 0; i < indices.length; i += 3) {
            int i0 = indices[i];
            int i1 = indices[i + 1];
            int i2 = indices[i + 2];
            Vec3 pos0 = new Vec3(positions[i0 * 3], positions[i0 * 3 + 1], positions[i0 * 3 + 2]);
            Vec3 pos1 = new Vec3(positions[i1 * 3], positions[i1 * 3 + 1], positions[i1 * 3 + 2]);
            Vec3 pos2 = new Vec3(positions[i2 * 3], positions[i2 * 3 + 1], positions[i2 * 3 + 2]);

            Vec3 v1 = new Vec3(pos1).sub(pos0);
            Vec3 v2 = new Vec3(pos2).sub(pos0);

            Vec3 normal = new Vec3(v1).cross(v2);
            if (normal.getLength() != 0)
                normal.normalize();

            normals[i0 * 3] += normal.x;
            normals[i0 * 3 + 1] += normal.y;
            normals[i0 * 3 + 2] += normal.z;

            normals[i1 * 3] += normal.x;
            normals[i1 * 3 + 1] += normal.y;
            normals[i1 * 3 + 2] += normal.z;

            normals[i2 * 3] += normal.x;
            normals[i2 * 3 + 1] += normal.y;
            normals[i2 * 3 + 2] += normal.z;
        }
        return normals;
    }

}
