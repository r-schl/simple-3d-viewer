package ecs.systems;

import ecs.EcsSystem;
import ecs.components.Position;
import linalib.Vec3;
import linalib.Vec3Readable;
import ecs.components.Oscillation;

public class OscillationSystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 30, this::onUpdate);
    }


    private void onUpdate() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities0(Oscillation.class)) {
                Oscillation stackMove = writable.getComponent0(entityId, Oscillation.class);
                Vec3Readable axis = stackMove.getAxis();
                float speed = stackMove.getSpeed();                
                float offset = calculateOffset(speed, 10, stackMove.getLifeTime());
                Vec3Readable centralPosition = stackMove.getCentralPosition();
                Vec3 position1 = new Vec3(centralPosition).add(new Vec3(axis).normalize().mul(offset));
                writable.putComponent0(entityId, new Position(position1));
            }
        });
    }

    private float calculateOffset(float speed, float maxOffset, long time) {
        float seconds = time / 1000f;
        float lambda = (maxOffset * 4) / speed;
        int numLambdaHalfs = (int) (seconds / (lambda * 0.5f));
        float secondsSinceLastMax = seconds % (lambda * 0.5f);
        if (numLambdaHalfs % 2 == 0)
            return speed * (secondsSinceLastMax - lambda * 0.25f);
        else
            return -speed * (secondsSinceLastMax - lambda * 0.25f);
    }

}
