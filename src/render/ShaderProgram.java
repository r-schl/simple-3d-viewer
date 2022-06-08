package render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import linalib.Mat4Readable;
import linalib.Vec2Readable;
import linalib.Vec3Readable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public abstract class ShaderProgram {

    protected Map<String, Integer> uniforms;

    private String vertexFile;
    private String fragmentFile;

    private int id;
    private int vertexShaderId;
    private int fragmentShaderId;

    // private HashMap<String, Integer> uniforms;

    public boolean isCreated;

    public ShaderProgram(String vertexFile, String fragmentFile) {
        this.vertexFile = vertexFile;
        this.fragmentFile = fragmentFile;
        isCreated = false;
    }

    public void create() {
        uniforms = new HashMap<>();
        vertexShaderId = loadShaderFromFile(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderId = loadShaderFromFile(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        this.id = GL20.glCreateProgram();

        // build up shader main program
        this.addShader(vertexShaderId);
        this.addShader(fragmentShaderId);
        this.bindAttributes();
        this.link();
        this.validate();
        this.getAllUniformLocations();
        isCreated = true;
    }

    private void addShader(int shaderId) {
        GL20.glAttachShader(this.id, shaderId);
    }

    private void link() {
        GL20.glLinkProgram(this.id);
        if (GL20.glGetProgrami(this.id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.err.println("ERROR: Program linking - " + GL20.glGetShaderInfoLog(this.id));
            System.exit(-1);
        }
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(this.id, attribute, variableName);
    }

    protected abstract void getAllUniformLocations();

    public void declareUniform(String uniform, String name) {
        int uniformLocation = GL20.glGetUniformLocation(this.id, name);
        if (uniformLocation == 0xFFFFFFFF) {
            new Exception("ERROR: Could not find uniform: \"" + name + "\"").printStackTrace();
            System.exit(-1);
        }
        uniforms.put(uniform, uniformLocation);
    }

    public void declareUniform(String name) {
        declareUniform(name, name);
    }

    public boolean hasUniform(String uniform) {
        return uniforms.containsKey(uniform);
    }

    // edit uniforms
    public void setUniform(String uniform, Vec3Readable vec) {
        if (!uniforms.containsKey(uniform))
            return;
        if (uniforms.get(uniform) != null) {
            GL20.glUniform3f(uniforms.get(uniform), (float) vec.getX(), (float) vec.getY(), (float) vec.getZ());
        }
    }

    public void setUniform(String uniform, Vec2Readable vector2f) {
        if (!uniforms.containsKey(uniform))
            return;
        if (uniforms.get(uniform) != null) {
            GL20.glUniform2f(uniforms.get(uniform), (float) vector2f.getX(), (float) vector2f.getY());
        }
    }

    public void setUniform(String uniform, double value) {
        if (!uniforms.containsKey(uniform))
            return;
        if (uniforms.get(uniform) != null) {
            GL20.glUniform1f(uniforms.get(uniform), (float) value);
        }
    }

    public void setUniform(String uniform, int value) {
        if (!uniforms.containsKey(uniform))
            return;
        if (uniforms.get(uniform) != null) {
            GL20.glUniform1i(uniforms.get(uniform), value);
        }
    }

    public void setUniform(String uniform, boolean value) {
        if (!uniforms.containsKey(uniform))
            return;
        if (uniforms.get(uniform) != null) {
            float toLoad = 0;
            if (value)
                toLoad = 1;
            GL20.glUniform1f(uniforms.get(uniform), toLoad);
        }
    }

    public void setUniform(String uniform, Mat4Readable mat) {
        if (uniforms.get(uniform) != null) {
            GL20.glUniformMatrix4fv(uniforms.get(uniform), false, BufferUtils.flippedFloat(mat));
        }
    }

    private void validate() {
        GL20.glValidateProgram(this.id);
        if (GL20.glGetProgrami(this.id, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            System.err.println("ERROR: Program validating - " + GL20.glGetShaderInfoLog(this.id));
            System.exit(-1);
        }
    }

    // start/stop
    public void start() {
        GL20.glUseProgram(this.id);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    private int loadShaderFromFile(String file, int type) {
        String shaderSource = readFile(file);
        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, shaderSource);
        compileShader(shaderId);
        return shaderId;
    }

    private void compileShader(int shaderId) {
        GL20.glCompileShader(shaderId);
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("ERROR (SHADER_FILE):    " + GL20.glGetShaderInfoLog(shaderId, 500));
            System.exit(-1);
        }
    }

    private String readFile(String file) {
        BufferedReader bufferedReader = null;
        StringBuilder string = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                string.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("ERROR: failed to read File");
        }
        return string.toString();
    }
}
