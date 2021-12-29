package ecs.components;

import ecs.Component;
import linalib.flt.FVec2Readable;

public class MouseCursor extends Component {

    private FVec2Readable position;

    public MouseCursor(FVec2Readable position) {
        this.position = position;
    }

    public FVec2Readable getPosition() {
        return this.position;
    }
    
}
