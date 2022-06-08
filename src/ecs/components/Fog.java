package ecs.components;

import ecs.Component;
import linalib.Vec3;
import linalib.Vec3Readable;


public class Fog extends Component {

    public static Fog STANDARD = new Fog(0, 1, new Vec3(0));
    public static Fog standard() {
        return STANDARD;
    }

    private double density;
    private double gradient;
    private Vec3Readable color;

    public Fog(double density, double gradient, Vec3Readable color) {
        this.density = density;
        this.gradient = gradient;
        this.color = color;
    }

    public double getDensity() {
        return this.density;
    }

    public double getGradient() {
        return this.gradient;
    }

    public Vec3Readable getColor() {
        return this.color;
    }

}
