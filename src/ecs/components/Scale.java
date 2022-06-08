package ecs.components;


import ecs.Component;
import linalib.Vec3;
import linalib.Vec3Readable;

public class Scale extends Component {

    public static Scale standard() {
        return new Scale(new Vec3(1));
    }

    private Vec3Readable vector;

    public Scale(Vec3Readable vector) {
        this.vector = vector;
    }

    public Vec3Readable getVector() {
        return this.vector;
    }

}
