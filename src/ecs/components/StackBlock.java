package ecs.components;

import ecs.Component;

public class StackBlock extends Component {
    
    public static StackBlock standard() {
        return new StackBlock();
    }
}
