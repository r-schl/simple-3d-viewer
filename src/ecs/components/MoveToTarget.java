package ecs.components;

import ecs.Component;
import linalib.flt.FVec3Readable;

public class MoveToTarget extends Component {

    private FVec3Readable target;
    private float speed;

    public MoveToTarget(FVec3Readable target, float speed) {
        this.target = target;
        this.speed = speed;
    }

    public FVec3Readable getTarget() {
        return this.target;
    }

    public float getSpeed() {
        return this.speed;
    }

}
