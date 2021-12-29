package ecs.components;

import ecs.Component;
import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

public class Velocity extends Component {

    public static Velocity standard() {
        return new Velocity(new FVec3(0));
    }

    private FVec3Readable v;

    public Velocity(FVec3Readable v) {
        this.v = v;
    }

    public FVec3Readable getVelocity() {
        return this.v;
    }

    public float getSpeed() {
        return this.v.len();
    }

}
