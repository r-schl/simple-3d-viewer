package ecs;

import java.sql.Timestamp;

public abstract class Event {

    Timestamp timestamp;
    EcsSystem sourceSystem;

    public Event() {
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public EcsSystem getSource() {
        return this.sourceSystem;
    }

    public void setSource(EcsSystem system) {
        this.sourceSystem = system;
    }

}
