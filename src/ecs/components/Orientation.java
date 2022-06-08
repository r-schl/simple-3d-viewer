package ecs.components;

import ecs.Component;
import linalib.Quaternion;
import linalib.QuaternionReadable;
import linalib.Vec3;

public class Orientation extends Component {

    public static Orientation standard() {
        return new Orientation(Quaternion.initRotation(Vec3.YAXIS, 0));
    }

    QuaternionReadable quaternion;

    public Orientation(QuaternionReadable quaternion) {
        this.quaternion = quaternion;
    }

    public QuaternionReadable getQuaternion() {
        return this.quaternion;
    }

    public Orientation getRotated(QuaternionReadable otherQuaternion) {
        Quaternion q = new Quaternion(this.quaternion);
        q.premul(otherQuaternion);
        return new Orientation(q);
    }

}
