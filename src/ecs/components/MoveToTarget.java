package ecs.components;

import ecs.Component;
import linalib.Vec3Readable;

public class MoveToTarget extends Component {

    private Vec3Readable target;
    private float speed;

    public MoveToTarget(Vec3Readable target, float speed) {
        this.target = target;
        this.speed = speed;
    }

    public Vec3Readable getTarget() {
        return this.target;
    }

    public float getSpeed() {
        return this.speed;
    }

}
