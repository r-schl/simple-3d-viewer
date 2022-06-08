package ecs.components;

import ecs.Component;
import linalib.Vec3;
import linalib.Vec3Readable;

public class AmbientLight extends Component {

    public static AmbientLight standard() {
        return new AmbientLight(new Vec3(1));
    }

    private Vec3Readable color;

    public AmbientLight(Vec3Readable color) {
        this.color = color;
    }

    public Vec3Readable getColor() {
        return this.color;
    }

 

}
