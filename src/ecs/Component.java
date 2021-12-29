package ecs;


public abstract class Component {

    long creationTime = System.currentTimeMillis();


    public static Component getStandard() {
        return null;
    }

    public long readTimer() {
        return System.currentTimeMillis() - creationTime;
    }    

}
