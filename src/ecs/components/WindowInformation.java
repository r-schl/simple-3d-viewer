package ecs.components;

import ecs.Component;

public class WindowInformation extends Component {
    
    private int width;
    private int height;

    public WindowInformation(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
