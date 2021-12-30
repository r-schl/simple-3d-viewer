package ecs.systems;

import java.util.Stack;

import org.lwjgl.glfw.GLFW;

import ecs.EcsSystem;
import ecs.components.AutoRotation;
import ecs.components.Keys;
import ecs.components.MeshReference;
import ecs.components.MoveToTarget;
import ecs.components.Scale;
import ecs.components.StackBlock;
import ecs.components.Oscillation;
import ecs.components.TextureReference;
import ecs.components.Position;
import linalib.flt.FVec3;
import linalib.flt.FVec3Readable;

public class BlockSpawnSystem extends EcsSystem {

    @Override
    public void init() {
        register("input key 32 1", 10, this::onSpace);
        register("input key 32 2", 10, this::onSpace);
    }

    int count = 0;

    private void onSpace() {
        store().write((writable) -> {

            writable.removeComponent(20 + count, Oscillation.class);

            Position pos1 = writable.getComponentOrStandard(20 + count, Position.class);
            Scale scale1 = writable.getComponentOrStandard(20 + count, Scale.class);

            if (count > 0) {
                Position pos0 = writable.getComponentOrStandard(20 + count - 1, Position.class);
                Scale scale0 = writable.getComponentOrStandard(20 + count - 1, Scale.class);

                if (count % 2 == 0) {
                    // swings on the negative x axis
                    float left1 = pos1.getVector().getX() - scale1.getVector().getX() / 2;
                    float right1 = pos1.getVector().getX() + scale1.getVector().getX() / 2;
                    float left0 = pos0.getVector().getX() - scale0.getVector().getX() / 2;
                    float right0 = pos0.getVector().getX() + scale0.getVector().getX() / 2;

                    float newScale = right0 - left1;
                    float posX = left1 + newScale / 2;
                    if (right1 - left0 < newScale) {
                        newScale = right1 - left0;
                        posX = left0 + newScale / 2;
                    }

                    if (newScale > 0) {
                        writable.putComponent(20 + count,
                        new Scale(new FVec3(newScale, scale1.getVector().getY(), scale1.getVector().getZ())),
                        new Position(new FVec3(posX, pos1.getVector().getY(), pos1.getVector().getZ())));
                    } else {
                        onGameOver();
                        return;
                    }
    

                } else {
                    // swings on the negative z axis
                    float left1 = pos1.getVector().getZ() - scale1.getVector().getZ() / 2;
                    float right1 = pos1.getVector().getZ() + scale1.getVector().getZ() / 2;
                    float left0 = pos0.getVector().getZ() - scale0.getVector().getZ() / 2;
                    float right0 = pos0.getVector().getZ() + scale0.getVector().getZ() / 2;

                    float newScale = right0 - left1;
                    float posZ = left1 + newScale / 2;
                    if (right1 - left0 < newScale) {
                        newScale = right1 - left0;
                        posZ = left0 + newScale / 2;
                    }
                    if (newScale > 0) {
                        writable.putComponent(20 + count,
                        new Scale(new FVec3(scale1.getVector().getX(), scale1.getVector().getY(), newScale)),
                        new Position(new FVec3(pos1.getVector().getX(), pos1.getVector().getY(), posZ)));
                    } else {
                        onGameOver();
                        return;                    }
                   
                }
            } 

            Scale scale = writable.getComponent(20 + count, Scale.class);
            Position pos = writable.getComponent(20 + count, Position.class);

            // new block
            count++;
            TextureReference tr = writable.getComponent(20, TextureReference.class);
            MeshReference mr = writable.getComponent(20, MeshReference.class);
            if (count % 2 == 0) {
                writable.putComponent(20 + count, tr, mr,
                        new Oscillation(new FVec3(FVec3.XAXIS).mul(-1), new FVec3(0, count, pos.getVector().getZ()),
                                8f),
                        scale, new StackBlock());
            } else {
                writable.putComponent(20 + count, tr, mr,
                        new Oscillation(new FVec3(FVec3.ZAXIS).mul(-1), new FVec3(pos.getVector().getX(), count, 0),
                                8f),
                        scale, new StackBlock());
            }

            writable.putComponent(15, new MoveToTarget(new FVec3(0, count, 0), 7f));
        });
    }

    private void onGameOver() {
        store().write((writable) -> {
            for (int entityId : writable.filterEntities(StackBlock.class)) {
                writable.removeEntity(entityId);
            }
            writable.removeComponent(15, MoveToTarget.class);
            writable.putComponent(15, new Position(new FVec3(0)));
        });
        count = 0;
    }

}
