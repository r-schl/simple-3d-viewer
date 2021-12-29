package ecs.systems;

import ecs.EcsSystem;
import ecs.components.Orientation;
import ecs.components.Position;
import ecs.components.FollowTarget;
import linalib.flt.FQuaternion;
import linalib.flt.FQuaternionReadable;
import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

public class FollowTargetSystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 2, this::onUpdate);
    }

    private void onUpdate() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities(FollowTarget.class)) {
                FollowTarget thirdPersonView = writable.getComponent(entityId, FollowTarget.class);
                int targetId = thirdPersonView.getEntityId();
                float distance = thirdPersonView.getDistance();
                FQuaternionReadable rotationRelativeToTarget = thirdPersonView.getRotation();
                
                Position targetPositionComponent = writable.getComponentOrStandard(targetId, Position.class);
                FVec3Readable targetPosition = targetPositionComponent.getVector();

                Orientation targetOrientation = writable.getComponentOrStandard(targetId, Orientation.class);
                FQuaternionReadable targetRotation = targetOrientation.getQuaternion();

                FQuaternion newRotation = new FQuaternion(targetRotation).premul(rotationRelativeToTarget).normalize();

                FVec3 forward = new FVec3(FVec3.ZAXIS).rotateQuaternion(newRotation).normalize();
                FVec3 newPosition = new FVec3(targetPosition).add(new FVec3(forward).mul(-distance));

                writable.putComponent(entityId, new Orientation(newRotation));
                writable.putComponent(entityId, new Position(newPosition));
            }
        });
    }

}
