package ecs.components;

import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

import ecs.Component;

public class Scale extends Component {

    public static Scale standard() {
        return new Scale(new FVec3(1));
    }

    private FVec3Readable vector;

    public Scale(FVec3Readable vector) {
        this.vector = vector;
    }

    public FVec3Readable getVector() {
        return this.vector;
    }

}
