package ecs.components;

import ecs.Component;
import linalib.flt.FVec3Readable;

public class AmbientLight extends Component {

    private FVec3Readable color;

    public AmbientLight(FVec3Readable color) {
        this.color = color;
    }

    public FVec3Readable getColor() {
        return this.color;
    }
}
