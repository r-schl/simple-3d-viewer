package ecs.systems;

import java.util.function.Consumer;

import ecs.EcsSystem;

import ecs.components.*;
import ecs.events.TestEvent;
import linalib.flt.*;

public class AutoRotationSystem extends EcsSystem {

    Orientation defaulOrientation = new Orientation(new FQuaternion().initRotation(0, FVec3.YAXIS));

    @Override
    public void init() {
        registerOwnThread("update", 0, this::onUpdate);
    }

    private void onUpdate() {
        store().write((writable) -> {
            for (int entity : writable.filterEntities(AutoRotation.class)) {
                Orientation orientation = writable.getComponent(entity, Orientation.class);
                if (orientation == null)
                    orientation = defaulOrientation;

                Orientation newOrientation = orientation.getRotated(FQuaternion.newRotation(0.2f, FVec3.YAXIS));
                // newOrientation = newOrientation.getRotated(FQuaternion.newRotation(1,
                // FVec3.XAXIS));
                writable.putComponent(entity, newOrientation);
            }
        });
    }

}
