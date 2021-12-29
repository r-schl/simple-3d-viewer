package ecs.components;

import ecs.Component;

public class Keys extends Component {
    
    private int[] keys;

    public Keys (int[] keys) {
        this.keys = keys;
    }

    public int getValue(int i) {
        return keys[i];
    }
}
