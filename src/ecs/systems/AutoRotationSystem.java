package ecs.systems;

import java.util.function.Consumer;

import ecs.EcsSystem;

import ecs.components.*;
import ecs.events.TestEvent;
import linalib.Quaternion;
import linalib.Vec3;

public class AutoRotationSystem extends EcsSystem {

    Orientation defaulOrientation = new Orientation(Quaternion.initRotation(Vec3.YAXIS, 0));

    @Override
    public void init() {
        registerOwnThread("update", 0, this::onUpdate);
    }

    private void onUpdate() {
        store().write((writable) -> {
            for (int entity : writable.filterEntities0(AutoRotation.class)) {
                Orientation orientation = writable.getComponent0(entity, Orientation.class);
                if (orientation == null)
                    orientation = defaulOrientation;

                Orientation newOrientation = orientation.getRotated(Quaternion.initRotation(Vec3.YAXIS, 0.2f));
                // newOrientation = newOrientation.getRotated(Quaternion.newRotation(1,
                // Vec3.XAXIS));
                writable.putComponent0(entity, newOrientation);
            }
        });
    }

}
