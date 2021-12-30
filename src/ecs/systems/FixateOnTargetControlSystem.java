package ecs.systems;

import org.lwjgl.glfw.GLFW;

import ecs.EcsSystem;
import ecs.components.Keys;
import ecs.components.MouseCursor;
import ecs.components.MouseScroll;
import ecs.components.Orientation;
import ecs.components.FixateOnTarget;
import ecs.components.FixateOnTargetControl;
import linalib.flt.FQuaternion;
import linalib.flt.FQuaternionReadable;
import linalib.flt.FVec2;
import linalib.flt.FVec2Readable;
import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

public class FixateOnTargetControlSystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 3, this::onUpdate);
        register("input scroll", 3, this::onScroll);
    }

    FVec2Readable mouseThisFrame = new FVec2(0);
    FVec2Readable mouseLastFrame = new FVec2(0);
    float zoomLevel;
    float pitchLevel;
    float angleLevel;
    boolean isScrolling = false;

    private void onScroll() {
        isScrolling = true;
    }

    private void onUpdate() {
        store().write((writable) -> {
            MouseCursor mouseCursor = writable.getComponent(0, MouseCursor.class);
            mouseThisFrame = mouseCursor.getPosition();
            Keys keys = writable.getComponent(0, Keys.class);
            MouseScroll mouseScroll = writable.getComponent(0, MouseScroll.class);

            for (int entityId : writable.filterEntities(FixateOnTargetControl.class)) {

                if (isScrolling) {
                    this.zoomLevel = mouseScroll.getScroll().getY() * -0.4f;
                    isScrolling = false;
                } else
                    zoomLevel = zoomLevel != 0 ? zoomLevel - zoomLevel / 15 : zoomLevel;

                if (keys.getValue(GLFW.GLFW_MOUSE_BUTTON_1) != 0) {
                    this.angleLevel = -new FVec2(mouseLastFrame).sub(mouseThisFrame).x * 0.3f;
                    this.pitchLevel = -new FVec2(mouseLastFrame).sub(mouseThisFrame).y * 0.3f;
                } else {
                    if (angleLevel != 0)
                        angleLevel = angleLevel - angleLevel / 7;
                    if (pitchLevel != 0)
                        pitchLevel = pitchLevel - pitchLevel / 7;
                }

                FixateOnTarget thirdPersonView = writable.getComponent(entityId, FixateOnTarget.class);
                if (thirdPersonView == null)
                    continue;

                int targetId = thirdPersonView.getEntityId();
                float distance0 = thirdPersonView.getDistance();
                FQuaternionReadable rotation0 = thirdPersonView.getRotation();
                FQuaternion rotation1;
                rotation1 = new FQuaternion(rotation0).premul(FQuaternion.newRotation(angleLevel, FVec3.YAXIS))
                        .normalize();
                FVec3 pitchRotationAxis = new FVec3(FVec3.XAXIS).rotateQuaternion(rotation1);
                rotation1 = new FQuaternion(rotation1).premul(FQuaternion.newRotation(pitchLevel, pitchRotationAxis))
                        .normalize();

                float distance1 = distance0 + zoomLevel;
                distance1 = distance1 < 0 ? 0 : distance1;

                writable.putComponent(entityId, new FixateOnTarget(targetId, distance1, rotation1));
            }

            mouseLastFrame = mouseThisFrame;

        });
    }

}
