package ecs.systems;

import org.lwjgl.glfw.GLFW;

import ecs.EcsSystem;
import ecs.components.Keys;
import ecs.components.MouseCursor;
import ecs.components.MouseScroll;
import ecs.components.Orientation;
import linalib.Quaternion;
import linalib.QuaternionReadable;
import linalib.Vec2;
import linalib.Vec2Readable;
import linalib.Vec3;
import ecs.components.FixateOnTarget;
import ecs.components.FixateOnTargetControl;

public class FixateOnTargetControlSystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 3, this::onUpdate);
        register("input scroll", 3, this::onScroll);
    }

    Vec2Readable mouseThisFrame = new Vec2(0);
    Vec2Readable mouseLastFrame = new Vec2(0);
    float zoomLevel;
    float pitchLevel;
    float angleLevel;
    boolean isScrolling = false;

    private void onScroll() {
        isScrolling = true;
    }

    private void onUpdate() {
        store().write((writable) -> {
            MouseCursor mouseCursor = writable.getComponent0(0, MouseCursor.class);
            mouseThisFrame = mouseCursor.getPosition();
            Keys keys = writable.getComponent0(0, Keys.class);
            MouseScroll mouseScroll = writable.getComponent0(0, MouseScroll.class);

            for (int entityId : writable.filterEntities0(FixateOnTargetControl.class)) {

                if (isScrolling) {
                    this.zoomLevel = mouseScroll.getScroll().getY() * -0.4f;
                    isScrolling = false;
                } else
                    zoomLevel = zoomLevel != 0 ? zoomLevel - zoomLevel / 15 : zoomLevel;

                if (keys.getValue(GLFW.GLFW_MOUSE_BUTTON_1) != 0) {
                    this.angleLevel = -new Vec2(mouseLastFrame).sub(mouseThisFrame).x * 0.3f;
                    this.pitchLevel = -new Vec2(mouseLastFrame).sub(mouseThisFrame).y * 0.3f;
                } else {
                    if (angleLevel != 0)
                        angleLevel = angleLevel - angleLevel / 7;
                    if (pitchLevel != 0)
                        pitchLevel = pitchLevel - pitchLevel / 7;
                }

                FixateOnTarget thirdPersonView = writable.getComponent0(entityId, FixateOnTarget.class);
                if (thirdPersonView == null)
                    continue;

                int targetId = thirdPersonView.getEntityId();
                float distance0 = thirdPersonView.getDistance();
                QuaternionReadable rotation0 = thirdPersonView.getRotation();
                Quaternion rotation1;
                rotation1 = new Quaternion(rotation0).premul(Quaternion.initRotation(Vec3.YAXIS, angleLevel))
                        .normalize();
                Vec3 pitchRotationAxis = new Vec3(Vec3.XAXIS).rotateByQuaternion(rotation1);
                rotation1 = new Quaternion(rotation1).premul(Quaternion.initRotation(pitchRotationAxis, pitchLevel))
                        .normalize();

                float distance1 = distance0 + zoomLevel;
                distance1 = distance1 < 0 ? 0 : distance1;

                writable.putComponent0(entityId, new FixateOnTarget(targetId, distance1, rotation1));
            }

            mouseLastFrame = mouseThisFrame;

        });
    }

}
