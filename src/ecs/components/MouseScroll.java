package ecs.components;

import ecs.Component;
import linalib.flt.FVec2;
import linalib.flt.FVec2Readable;

public class MouseScroll extends Component {

    private FVec2Readable scroll;

    public MouseScroll(FVec2Readable scroll) {
        this.scroll = scroll;
    }

    public FVec2Readable getScroll() {
        return this.scroll;
    }
}
