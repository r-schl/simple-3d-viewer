package ecs.components;

import ecs.Component;
import linalib.flt.FVec3Readable;

public class Oscillation extends Component {

    FVec3Readable axis;
    FVec3Readable centralPosition;

    /**
     * Speed in meters per second (m * s^-1)
     */
    float speed;

    public Oscillation(FVec3Readable axis, FVec3Readable centralPosition, float speed) {
        this.axis = axis;
        this.speed = speed;
        this.centralPosition = centralPosition;
    }

    public FVec3Readable getCentralPosition() {
        return this.centralPosition;
    }

    public FVec3Readable getAxis() {
        return this.axis;
    }

    public float getSpeed() {
        return this.speed;
    }

}
