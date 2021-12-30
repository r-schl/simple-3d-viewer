package ecs.components;

import ecs.Component;
import linalib.flt.FQuaternion;
import linalib.flt.FQuaternionReadable;
import linalib.flt.FVec3;

public class FixateOnTarget extends Component {

    private int entityId;

    // distance to the entity
    private float distance;
    // rotation of the camera relative to the entities forward vector
    private FQuaternionReadable rotation;

    public FixateOnTarget(int entityId, float distance, FQuaternionReadable rotation) {
        this.entityId = entityId;
        this.distance = distance;
        this.rotation = rotation;
    }

    public FixateOnTarget(int entityId, float distance) {
        this(entityId, distance, FQuaternion.newRotation(0, FVec3.YAXIS));
    }

    public int getEntityId() {
        return this.entityId;
    }

    public float getDistance() {
        return this.distance;
    }

    public FQuaternionReadable getRotation() {
        return this.rotation;
    }

}
