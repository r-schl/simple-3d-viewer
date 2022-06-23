package ecs.systems;

import ecs.EcsSystem;
import ecs.components.MoveToTarget;
import ecs.components.Position;
import ecs.components.Timer;
import linalib.Vec3;
import linalib.Vec3Readable;

public class MoveToTargetSystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 30, this::onUpdate);
    }

    private void onUpdate() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities0(MoveToTarget.class)) {
                MoveToTarget moveToTarget = writable.getComponent0(entityId, MoveToTarget.class);
                Vec3Readable target = moveToTarget.getTarget();
                float speed = moveToTarget.getSpeed();
                Vec3Readable position = writable.getComponentOrStandard0(entityId, Position.class).getVector();

                Timer timer = writable.getComponent(this.getSystemId(), entityId, Timer.class);
                long deltaTime = timer == null ? moveToTarget.getLifeTime() : timer.read();

                Vec3 positionToTarget = new Vec3(target).sub(position);

                if (positionToTarget.getLength() == 0)
                    continue;
                Vec3 d = new Vec3(positionToTarget).normalize().mul(speed).mul(deltaTime / 1000f);

                if (positionToTarget.getLength() >= d.getLength()) {
                    Vec3 position1 = new Vec3(position).add(d);
                    writable.putComponent0(entityId, new Position(position1));
                } else {
                    writable.putComponent0(entityId, new Position(target));
                }

                writable.putComponent(this.getSystemId(), entityId, new Timer());
            }
        });
    }
    
}
