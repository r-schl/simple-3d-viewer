package ecs.components;

import linalib.flt.FQuaternion;
import linalib.flt.FQuaternionReadable;
import linalib.flt.FVec3;
import ecs.Component;

public class Orientation extends Component {

    public static Orientation standard() {
        return new Orientation(new FQuaternion().initRotation(0, FVec3.YAXIS));
    }

    FQuaternionReadable quaternion;

    public Orientation(FQuaternionReadable quaternion) {
        this.quaternion = quaternion;
    }

    public FQuaternionReadable getQuaternion() {
        return this.quaternion;
    }

    public Orientation getRotated(FQuaternionReadable otherQuaternion) {
        FQuaternion q = new FQuaternion(this.quaternion);
        q.premul(otherQuaternion);
        return new Orientation(q);
    }

}
