package ecs.components;

import ecs.Component;
import linalib.flt.FVec3Readable;

public class DirectionalLight extends Component {

    private FVec3Readable direction;
    private FVec3Readable color;
    private double intensity;

    public DirectionalLight(FVec3Readable direction, FVec3Readable color, double intensity) {
        this.direction = direction;
        this.color = color;
        this.intensity = intensity;
    }

    public FVec3Readable getDirection() {
        return this.direction;
    }

    public FVec3Readable getColor() {
        return this.color;
    }

    public double getIntensity() {
        return this.intensity;
    }
}
