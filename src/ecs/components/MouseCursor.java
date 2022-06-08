package ecs.components;

import ecs.Component;
import linalib.Vec2Readable;

public class MouseCursor extends Component {

    private Vec2Readable position;

    public MouseCursor(Vec2Readable position) {
        this.position = position;
    }

    public Vec2Readable getPosition() {
        return this.position;
    }
    
}
