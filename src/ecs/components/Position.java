package ecs.components;

import ecs.Component;
import linalib.Vec3;
import linalib.Vec3Readable;

public class Position extends Component {

    public static Position STANDARD = new Position(new Vec3(0));

    public static Position standard() {
        return STANDARD;
    }

    private Vec3Readable vector;

    public Position(Vec3Readable vector) {
        this.vector = vector;
    }

    public Vec3Readable getVector() {
        return this.vector;
    }
}
