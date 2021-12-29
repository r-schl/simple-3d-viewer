package ecs.components;

import ecs.Component;
import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

public class Position extends Component {

    public static Position standard() {
        return new Position(new FVec3(0));
    }

    private FVec3Readable vector;

    public Position(FVec3Readable vector) {
        this.vector = vector;
    }

    public FVec3Readable getVector() {
        return this.vector;
    }
}
