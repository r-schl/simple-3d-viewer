
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import ecs.EventDispatcher;
import ecs.MeshLoader;
import ecs.components.*;
import ecs.systems.*;
import linalib.Quaternion;
import linalib.Vec3;
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
                writable.putComponent0(10, new Camera(90, 0.1f, 1000f));

                Quaternion rotation = Quaternion.initRotation(Vec3.YAXIS, 45);
                rotation.premul(Quaternion.initRotation(new Vec3(Vec3.XAXIS).rotateByQuaternion(rotation), 30));
                writable.putComponent0(10, new FixateOnTarget(15, 18, rotation));
                writable.putComponent0(10, new FixateOnTargetControl());

                // Ambient light
                writable.putComponent0(1, new AmbientLight(new Vec3(0.4f)));

                // Directional light
                writable.putComponent0(2,
                        new LightPlane(new Vec3(1), 1),
                        new Orientation(Quaternion.initRotation(Vec3.YAXIS, 0)));

                writable.putComponent0(15, new Position(new Vec3(0, 0, 0)));

                TextureReference tr = new TextureLoader().loadPNGFile("src/res/textures/Unbenannt.png");
                MeshReference mr = new MeshLoader().loadOBJFile("src/res/objects/cube.obj");

                MeshReference mr2 = new MeshLoader().loadOBJFile("src/res/objects/dragon.obj");

                writable.putComponent0(20, tr, mr, new Position(new Vec3(0, 0, 0)), new Scale(new Vec3(8, 1, 8)));

                // writable.putComponent0(19, tr, mr2,
                // new MoveToTarget(new Vec3(0, 10, 10), 4f));

            });

        });

        Render renderSystem = new Render(new Vec3(0));
        renderSystem.set(store, eventDispatcher);
        renderSystem.start();

        Window windowSystem = new Window(800, 800, "Test");
        windowSystem.set(store, eventDispatcher);
        windowSystem.start();

        AutoRotationSystem autoRotation = new AutoRotationSystem();
        autoRotation.set(store, eventDispatcher);
        autoRotation.startOnThread();

        FixateOnTargetControlSystem thirdPersonViewInputSystem = new FixateOnTargetControlSystem();
        thirdPersonViewInputSystem.set(store, eventDispatcher);
        thirdPersonViewInputSystem.start();

        FixateOnTargetSystem thirdPersonViewSystem = new FixateOnTargetSystem();
        thirdPersonViewSystem.set(store, eventDispatcher);
        thirdPersonViewSystem.start();

        OscillationSystem blockMoveSystem = new OscillationSystem();
        blockMoveSystem.set(store, eventDispatcher);
        blockMoveSystem.start();

        BlockSpawnSystem blockSpawnSystem = new BlockSpawnSystem();
        blockSpawnSystem.set(store, eventDispatcher);
        blockSpawnSystem.start();

        VelocitySystem velocitySystem = new VelocitySystem();
        velocitySystem.set(store, eventDispatcher);
        velocitySystem.start();

        MoveToTargetSystem moveToTargetSystem = new MoveToTargetSystem();
        moveToTargetSystem.set(store, eventDispatcher);
        moveToTargetSystem.start();

        Core coreSystem = new Core(60, 60);
        coreSystem.set(store, eventDispatcher);
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