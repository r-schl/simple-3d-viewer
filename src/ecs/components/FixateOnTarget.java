package ecs.components;

import ecs.Component;
import linalib.Quaternion;
import linalib.QuaternionReadable;
import linalib.Vec3;

public class FixateOnTarget extends Component {

    private int entityId;

    // distance to the entity
    private float distance;
    // rotation of the camera relative to the entities forward vector
    private QuaternionReadable rotation;

    public FixateOnTarget(int entityId, float distance, QuaternionReadable rotation) {
        this.entityId = entityId;
        this.distance = distance;
        this.rotation = rotation;
    }

    public FixateOnTarget(int entityId, float distance) {
        this(entityId, distance, Quaternion.initRotation(Vec3.YAXIS, 0));
    }

    public int getEntityId() {
        return this.entityId;
    }

    public float getDistance() {
        return this.distance;
    }

    public QuaternionReadable getRotation() {
        return this.rotation;
    }

}
