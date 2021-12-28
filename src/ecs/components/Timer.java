package ecs.components;

import java.util.Date;

import ecs.Component;

public class Timer extends Component {

    private long startTime;

    public Timer() {
        this.startTime = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - startTime;
    }

}
