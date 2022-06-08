package ecs.components;

import ecs.Component;
import linalib.Vec3;
import linalib.Vec3Readable;

public class LightPlane extends Component {

    public static LightPlane standard() {
        return new LightPlane(new Vec3(1), 1);
    }

    private Vec3Readable color;
    private double intensity;

    public LightPlane(Vec3Readable color, double intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public Vec3Readable getColor() {
        return this.color;
    }

    public double getIntensity() {
        return this.intensity;
    }

}
