
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjglx.util.vector.Quaternion;

import ecs.EventDispatcher;
import ecs.MeshLoader;
import ecs.components.*;
import ecs.systems.*;
import linalib.flt.FQuaternion;
import linalib.flt.FVec3;
import ecs.Store;
import ecs.TextureLoader;

class Main {

    public static void main(String[] args) {
        new Main();
    }

    Main() {
        Store store = new Store();

        EventDispatcher eventDispatcher = new EventDispatcher();

        eventDispatcher.register("window:created", 0, () -> {
            store.write((writable) -> {

                // camera
                writable.putComponent(10, new Camera(90, 0.1f, 1000f));

                FQuaternion rotation = FQuaternion.newRotation(45, FVec3.YAXIS);
                rotation.premul(FQuaternion.newRotation(30, new FVec3(FVec3.XAXIS).rotateQuaternion(rotation)));
                writable.putComponent(10, new FixateOnTarget(15, 18, rotation));
                writable.putComponent(10, new FixateOnTargetControl());

                // environment
                writable.putComponent(1, new DirectionalLight(new FVec3(0, -0.8f, 0.8f), new FVec3(1), 0.9),
                        new AmbientLight(new FVec3(0.4f)));

                writable.putComponent(15, new Position(new FVec3(0, 0, 0)));

                TextureReference tr = new TextureLoader().loadPNGFile("src/res/textures/Unbenannt.png");
                MeshReference mr = new MeshLoader().loadOBJFile("src/res/objects/cube.obj");

                MeshReference mr2 = new MeshLoader().loadOBJFile("src/res/objects/dragon.obj");

                writable.putComponent(20, tr, mr, new Position(new FVec3(0, 0, 0)), new Scale(new FVec3(8, 1, 8)));

                /*
                 * writable.putComponent(19, tr, mr2,
                 * new Position(new FVec3(0, 0, 0)),
                 * new MoveToTarget(new FVec3(0, 10, 10), 4f));
                 */

            });

        });

        Render renderSystem = new Render(new FVec3(0));
        renderSystem.setStore(store);
        renderSystem.setEventDispatcher(eventDispatcher);
        renderSystem.start();

        Window windowSystem = new Window(800, 800, "Test");
        windowSystem.setStore(store);
        windowSystem.setEventDispatcher(eventDispatcher);
        windowSystem.start();

        AutoRotationSystem autoRotation = new AutoRotationSystem();
        autoRotation.setStore(store);
        autoRotation.setEventDispatcher(eventDispatcher);
        autoRotation.startOnThread();

        FixateOnTargetControlSystem thirdPersonViewInputSystem = new FixateOnTargetControlSystem();
        thirdPersonViewInputSystem.setStore(store);
        thirdPersonViewInputSystem.setEventDispatcher(eventDispatcher);
        thirdPersonViewInputSystem.start();

        FixateOnTargetSystem thirdPersonViewSystem = new FixateOnTargetSystem();
        thirdPersonViewSystem.setStore(store);
        thirdPersonViewSystem.setEventDispatcher(eventDispatcher);
        thirdPersonViewSystem.start();

        OscillationSystem blockMoveSystem = new OscillationSystem();
        blockMoveSystem.setStore(store);
        blockMoveSystem.setEventDispatcher(eventDispatcher);
        blockMoveSystem.start();

        BlockSpawnSystem blockSpawnSystem = new BlockSpawnSystem();
        blockSpawnSystem.setStore(store);
        blockSpawnSystem.setEventDispatcher(eventDispatcher);
        blockSpawnSystem.start();

        MovementSystem velocitySystem = new MovementSystem();
        velocitySystem.setStore(store);
        velocitySystem.setEventDispatcher(eventDispatcher);
        velocitySystem.start();

        Core coreSystem = new Core(60, 60);
        coreSystem.setStore(store);
        coreSystem.setEventDispatcher(eventDispatcher);
        coreSystem.startOnThread();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        eventDispatcher.trigger("window:create");

    }

}