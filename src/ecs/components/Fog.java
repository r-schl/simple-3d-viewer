package ecs.components;

import ecs.Component;
import linalib.flt.FVec3Readable;

public class Fog extends Component {
    private double density;
    private double gradient;
    private FVec3Readable color;

    public Fog(double density, double gradient, FVec3Readable color) {
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

    public FVec3Readable getColor() {
        return this.color;
    }
}
