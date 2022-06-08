package render;

public class PhongShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/res/shaders/vertex.txt";
    private static final String FRAGMENT_FILE = "src/res/shaders/fragment.txt";

    public PhongShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        super.declareUniform("MWMatrix");
        super.declareUniform("MWVPMatrix");
       super.declareUniform("cameraPosition");
        super.declareUniform("textureMat");
       super.declareUniform("textureRepeat");
        super.declareUniform("ambientLight");
        super.declareUniform("baseColor");
        super.declareUniform("dye");
        super.declareUniform("fogDensity");
        super.declareUniform("fogColor");
       super.declareUniform("fogGradient");
        super.declareUniform("directionalLight.base.color");
        super.declareUniform("directionalLight.base.intensity");
       super.declareUniform("directionalLight.direction");
    }


}

