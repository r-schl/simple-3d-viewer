package ecs.systems;

import ecs.EcsSystem;
import ecs.components.MoveToTarget;
import ecs.components.Position;
import ecs.components.Timer;
import ecs.components.Velocity;
import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

public class MovementSystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 20, this::onUpdate);
    }

    long lastTime;

    private void onUpdate() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities(Velocity.class)) {
                FVec3Readable velocity = writable.getComponent(entityId, Velocity.class).getVelocity();
                FVec3Readable position = writable.getComponentOrStandard(entityId, Position.class).getVector();

                long thisTime = writable.getComponent(0, Timer.class).readTimer();
                long deltaTime = thisTime - lastTime;

                FVec3 d = new FVec3(velocity).mul(deltaTime / 1000f);
                writable.putComponent(entityId, new Position(new FVec3(position).add(d)));
            }

            for (int entityId : writable.filterEntities(MoveToTarget.class)) {
                MoveToTarget moveToTarget = writable.getComponent(entityId, MoveToTarget.class);
                FVec3Readable target = moveToTarget.getTarget();
                float speed = moveToTarget.getSpeed();
                FVec3Readable position = writable.getComponentOrStandard(entityId, Position.class).getVector();

                long thisTime = writable.getComponent(0, Timer.class).readTimer();
                long deltaTime = thisTime - lastTime;

                FVec3 positionToTarget = new FVec3(target).sub(position);

                if (positionToTarget.len() == 0) continue;
                FVec3 d = new FVec3(positionToTarget).normalize().mul(speed).mul(deltaTime / 1000f);

                if (positionToTarget.len() >= d.len()) {
                    FVec3 position1 = new FVec3(position).add(d);
                    writable.putComponent(entityId, new Position(position1));
                } else {
                    writable.putComponent(entityId, new Position(target));
                }
            }

            lastTime = writable.getComponent(0, Timer.class).readTimer();
        });
    }

}
