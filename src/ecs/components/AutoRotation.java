package ecs.components;

import ecs.Component;

public class AutoRotation extends Component {

    public static AutoRotation standard() {
        return new AutoRotation();
    }
}
