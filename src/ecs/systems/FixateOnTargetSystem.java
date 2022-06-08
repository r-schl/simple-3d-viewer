package ecs.systems;

import ecs.EcsSystem;
import ecs.components.Orientation;
import ecs.components.Position;
import linalib.Quaternion;
import linalib.QuaternionReadable;
import linalib.Vec3;
import linalib.Vec3Readable;
import ecs.components.FixateOnTarget;

public class FixateOnTargetSystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 2, this::onUpdate);
    }

    private void onUpdate() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities0(FixateOnTarget.class)) {
                FixateOnTarget thirdPersonView = writable.getComponent0(entityId, FixateOnTarget.class);
                int targetId = thirdPersonView.getEntityId();
                float distance = thirdPersonView.getDistance();
                QuaternionReadable rotationRelativeToTarget = thirdPersonView.getRotation();
                
                Position targetPositionComponent = writable.getComponentOrStandard0(targetId, Position.class);
                Vec3Readable targetPosition = targetPositionComponent.getVector();

                Orientation targetOrientation = writable.getComponentOrStandard0(targetId, Orientation.class);
                QuaternionReadable targetRotation = targetOrientation.getQuaternion();

                Quaternion newRotation = new Quaternion(targetRotation).premul(rotationRelativeToTarget).normalize();

                Vec3 forward = new Vec3(Vec3.ZAXIS).rotateByQuaternion(newRotation).normalize();
                Vec3 newPosition = new Vec3(targetPosition).add(new Vec3(forward).mul(-distance));

                writable.putComponent0(entityId, new Orientation(newRotation));
                writable.putComponent0(entityId, new Position(newPosition));
            }
        });
    }

}
