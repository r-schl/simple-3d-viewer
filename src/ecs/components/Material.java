package ecs.components;

import linalib.Vec3;
import linalib.Vec3Readable;

public class Material {

    // Table of materials at
    // http://devernay.free.fr/cours/opengl/materials.html
    public static Material gold() {
        return new Material(
                new Vec3(0.24725f, 0.1995f, 0.0745f),
                new Vec3(0.75164f, 0.60648f, 0.22648f),
                new Vec3(0.628281f, 0.555802f, 0.366065f),
                0.4f);
    }

    private Vec3Readable ambient;
    private Vec3Readable diffuse;
    private Vec3Readable specular;
    private float shininess;

    public Material(Vec3Readable ambient, Vec3Readable diffuse, Vec3Readable specular, float shininess) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    public Vec3Readable getAmbient() {
        return this.ambient;
    }

    public Vec3Readable getDiffuse() {
        return this.diffuse;
    }

    public Vec3Readable getSpecular() {
        return this.specular;
    }

    public float getShininess() {
        return this.shininess;
    }
}
