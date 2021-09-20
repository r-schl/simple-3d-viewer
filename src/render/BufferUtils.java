package render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import linalib.flt.*;

public class BufferUtils {

    public static FloatBuffer floatBuffer(int size) {
        return org.lwjgl.BufferUtils.createFloatBuffer(size);
    }

    public static IntBuffer intBuffer(int size) {
        return org.lwjgl.BufferUtils.createIntBuffer(size);
    }

    public static IntBuffer flippedInt(int[] values) {
        IntBuffer buffer = intBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }

    public static FloatBuffer flippedFloat(FMat4Readable matrix) {
        FloatBuffer buffer = floatBuffer(4 * 4);
        // value.store(buffer);
        matrix.storeInside(buffer);
        buffer.flip();
        return buffer;
    }

    public static FloatBuffer flippedFloat(double[] values) {
        FloatBuffer buffer = floatBuffer(values.length);
        for (double value : values) {
            buffer.put((float) value);
        }
        buffer.flip();
        return buffer;
    }

    public static FloatBuffer flippedFloat(float[] values) {
        FloatBuffer buffer = floatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }
}
