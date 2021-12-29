package ecs.systems;

import ecs.EcsSystem;
import ecs.components.Timer;
import ecs.components.Velocity;
import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

public class VelocitySystem extends EcsSystem {

    @Override
    public void init() {
        // TODO Auto-generated method stub
        register("update", 0, this::onUpdate);
    }

    long lastTime;

    private void onUpdate() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities(Velocity.class)) {
                FVec3Readable velocity = writable.getComponent(entityId, Velocity.class).getVelocity();
                
                long thisTime = writable.getComponent(0, Timer.class).readTimer();
                long deltaTime = thisTime - lastTime;
                


            }
        });
    }

}
