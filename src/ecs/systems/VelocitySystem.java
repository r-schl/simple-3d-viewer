package ecs.systems;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import ecs.Component;
import ecs.EcsSystem;
import ecs.components.MoveToTarget;
import ecs.components.Position;
import ecs.components.Timer;
import ecs.components.Velocity;
import linalib.Vec3;
import linalib.Vec3Readable;

public class VelocitySystem extends EcsSystem {

    @Override
    public void init() {
        register("update", 30, this::onUpdate);
    }

    private void onUpdate() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities0(Velocity.class)) {
                Velocity velocityComponent = writable.getComponent0(entityId, Velocity.class);
                Vec3Readable velocity = velocityComponent.getVelocity();
                Vec3Readable position = writable.getComponentOrStandard0(entityId, Position.class).getVector();

                Timer timer = writable.getComponent(this.getSystemId(), entityId, Timer.class);
                long deltaTime = timer == null ? velocityComponent.getLifeTime() : timer.read();

                Vec3 d = new Vec3(velocity).mul(deltaTime / 1000f);
                writable.putComponent0(entityId, new Position(new Vec3(position).add(d)));
                writable.putComponent(this.getSystemId(), entityId, new Timer());
            }
        });
    }

    public long StringToLong(String string) {
        byte[] bytes = string.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();// need flip
        return buffer.getInt();
    }

}
