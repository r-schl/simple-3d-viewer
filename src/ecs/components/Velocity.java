package ecs.components;

import ecs.Component;
import linalib.flt.FVec3Readable;

public class Velocity extends Component {

    private FVec3Readable v;

    public Velocity(FVec3Readable v) {
        this.v = v;
    }

    public FVec3Readable getVector() {
        return this.v;
    }

}
