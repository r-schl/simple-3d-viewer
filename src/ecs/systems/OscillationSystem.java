package ecs.systems;

import ecs.EcsSystem;
import ecs.components.Position;
import ecs.components.Oscillation;
import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

public class OscillationSystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 0, this::onUpdate);
    }


    private void onUpdate() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities(Oscillation.class)) {
                Oscillation stackMove = writable.getComponent(entityId, Oscillation.class);
                FVec3Readable axis = stackMove.getAxis();
                float speed = stackMove.getSpeed();

                Position positionComponent = writable.getComponentOrStandard(entityId, Position.class);
                
                float offset = calculateOffset(speed, 10, stackMove.readTimer());

                FVec3Readable centralPosition = stackMove.getCentralPosition();
                FVec3 position1 = new FVec3(centralPosition).add(new FVec3(axis).normalize().mul(offset));
                writable.putComponent(entityId, new Position(position1));
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
