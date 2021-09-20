package ecs.events;

import ecs.Event;

public class TestEvent extends Event {

    private String message;

    public TestEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
