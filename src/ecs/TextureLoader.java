package ecs;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import ecs.components.TextureReference;

public class TextureLoader {

    public TextureReference loadPNGFile(String path) {
        org.newdawn.slick.opengl.Texture tex = null;
        try {
            tex = org.newdawn.slick.opengl.TextureLoader.getTexture("PNG", new FileInputStream(path));
            GL30.glGenerateMipmap(GL_TEXTURE_2D);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.7f);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            // enable pix like texture appearance
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[TEXTURE]: Failed to load texture from file");
            System.exit(-1);
        }
        return new TextureReference(tex.getTextureID(), false);
    }
}
