package ecs.components;

import linalib.flt.FQuaternion;
import linalib.flt.FQuaternionReadable;

import ecs.Component;

public class Orientation extends Component {

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
