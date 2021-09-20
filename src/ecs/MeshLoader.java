package ecs;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import render.BufferUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import ecs.components.*;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class MeshLoader {

    public static final int POS_STORE_ATTR_NUM = 0;
    public static final int TEX_STORE_ATTR_NUM = 1;
    public static final int NORM_STORE_ATTR_NUM = 2;

    private final static int POS_SIZE = 3;
    private final static int TEX_SIZE = 2;
    private final static int NORM_SIZE = 3;

    public synchronized MeshReference loadToVao(float[] positions, float[] texCoordinates, float[] normals, int[] indices) {
        return loadToVao(
                BufferUtils.flippedFloat(positions),
                BufferUtils.flippedFloat(texCoordinates),
                BufferUtils.flippedFloat(normals),
                BufferUtils.flippedInt(indices),
                positions.length,
                indices.length
        );
    }

    private synchronized MeshReference loadToVao(FloatBuffer positions, FloatBuffer texCoordinates, FloatBuffer normals, IntBuffer indices, int numberOfPositions, int numberOfIndices) {
        int vao = GL30.glGenVertexArrays();  // create VAO
        glBindVertexArray(vao); // open VAO
        int posVbo = storeBufferInAttrList(POS_STORE_ATTR_NUM, POS_SIZE, positions);
        int texVbo = storeBufferInAttrList(TEX_STORE_ATTR_NUM, TEX_SIZE, texCoordinates);
        int normVbo = storeBufferInAttrList(NORM_STORE_ATTR_NUM, NORM_SIZE, normals);

        // store indices in vao
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        glBindVertexArray(0); // close VAO
        return new MeshReference(vao, numberOfPositions, numberOfIndices);
    }


    private synchronized static int storeBufferInAttrList(int attrNumber, int size, FloatBuffer buffer) {
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL_ARRAY_BUFFER, vbo); //open vbo
        GL15.glBufferData(GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); //store data
        GL20.glVertexAttribPointer(attrNumber, size, GL11.GL_FLOAT, false, 0, 0); //create pointer
        GL15.glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public MeshReference loadOBJFile(String path) {

        // Read an OBJ file
        InputStream objInputStream = null;
        try {
            objInputStream = new FileInputStream(path);
            Obj largeObj = ObjReader.read(objInputStream);
            Obj obj = ObjUtils.convertToRenderable(largeObj);

            return MeshReference.load(ObjData.getVerticesArray(obj),
                    ObjData.getTexCoordsArray(obj, 2),
                    //ObjData.getNormalsArray(obj),
                    ObjData.getFaceVertexIndicesArray(obj, 3)
            );


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

