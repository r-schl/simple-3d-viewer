package ecs.components;

import linalib.flt.FVec3Readable;

import ecs.Component;

public class Scale extends Component {

    private FVec3Readable vector;

    public Scale(FVec3Readable vector) {
        this.vector = vector;
    }

    public FVec3Readable getVector() {
        return this.vector;
    }

}
