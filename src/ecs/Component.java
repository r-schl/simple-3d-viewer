package ecs;


public abstract class Component {

    long creationTime = System.currentTimeMillis();

    public long getLifeTime() {
        return System.currentTimeMillis() - this.creationTime;
    }

}
