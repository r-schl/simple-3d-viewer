package ecs.components;

import ecs.Component;
import linalib.Vec3Readable;

public class Oscillation extends Component {

    Vec3Readable axis;
    Vec3Readable centralPosition;

    /**
     * Speed in meters per second (m * s^-1)
     */
    float speed;

    public Oscillation(Vec3Readable axis, Vec3Readable centralPosition, float speed) {
        this.axis = axis;
        this.speed = speed;
        this.centralPosition = centralPosition;
    }

    public Vec3Readable getCentralPosition() {
        return this.centralPosition;
    }

    public Vec3Readable getAxis() {
        return this.axis;
    }

    public float getSpeed() {
        return this.speed;
    }

}
