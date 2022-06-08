package ecs.components;

import ecs.Component;
import linalib.Vec3;
import linalib.Vec3Readable;


public class Velocity extends Component {

    public static Velocity standard() {
        return new Velocity(new Vec3(0));
    }

    private Vec3Readable v;

    public Velocity(Vec3Readable v) {
        this.v = v;
    }

    public Vec3Readable getVelocity() {
        return this.v;
    }

    public float getSpeed() {
        return this.v.getLen();
    }

}
