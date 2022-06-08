package ecs.components;

import ecs.Component;
import linalib.Vec2Readable;


public class MouseScroll extends Component {

    private Vec2Readable scroll;

    public MouseScroll(Vec2Readable scroll) {
        this.scroll = scroll;
    }

    public Vec2Readable getScroll() {
        return this.scroll;
    }
}
