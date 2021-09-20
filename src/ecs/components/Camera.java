package ecs.components;

import ecs.Component;


public class Camera extends Component {

    private float fov;
    private float zFar;
    private float zNear;

    public Camera(float fov, float zNear, float zFar) {
        this.fov = fov;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public float getFOV() {
        return this.fov;
    }

    public float getZFar() {
        return this.zFar;
    }

    public float getZNear() {
        return this.zNear;
    }

}
