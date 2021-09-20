package ecs.components;

import ecs.Component;

public class LoopInformation extends Component {

    private int currentFPS;
    private int currentUPS;

    public LoopInformation(int currentFPS, int currentUPS) {
        this.currentFPS = currentFPS;
        this.currentUPS = currentUPS;
    }

    public int getCurrentFPS() {
        return this.currentFPS;
    }

    public int getCurrentUPS() {
        return this.currentUPS;
    }
}
