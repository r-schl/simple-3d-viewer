
import java.util.Arrays;

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
                writable.putComponent(10, new Orientation(FQuaternion.newRotation(0, FVec3.YAXIS)));
                writable.putComponent(10, new AutoRotation());

                // environment
                writable.putComponent(1, new DirectionalLight(new FVec3(0, 0.8f, 0.8f), new FVec3(1), 0.9),
                        new AmbientLight(new FVec3(0.4f)));

                // object
                writable.putComponent(24, new TextureLoader().loadPNGFile("src/res/textures/Unbenannt.png"),
                        new MeshLoader().loadOBJFile("src/res/objects/dragon.obj"), new Position(new FVec3(0, 0, -10)));
                // writable.putComponent(24, new AutoRotation());

                writable.putComponent(24, new AutoRotation());

                // int[] entities = writable.filterEntities(TextureReference.class);
                // System.out.println(Arrays.toString(entities));
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