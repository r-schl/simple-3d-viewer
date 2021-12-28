package ecs.systems;

import ecs.EcsSystem;
import ecs.components.LoopInformation;
import ecs.components.Timer;

public class Core extends EcsSystem {

    double ups;
    double fps;
    boolean isRunning = true;
    int ticks = 0; // for information
    int frames = 0; // for information
    int seconds = 0;
    int min = 0;
    public double timeU;
    public double timeF;


    public Core(double ups, double fps) {
        this.ups = ups;
        this.fps = fps;
        timeF = 1000000000 / fps;
        timeU = 1000000000 / ups;
    }

    @Override
    public void init() {
        register("window:created", -1, this::onStartLoop);
        register("stop", -2, this::onStop);
    }

    private void onStop() {
        print("stopped");
        System.exit(0);
    }

    private void onStartLoop() {
            // Start a timer
            store().write((writable) -> writable.putComponent(0, new Timer()));

            long timer = System.currentTimeMillis(); // for information
            double lastTime = System.nanoTime();
            double deltaU = 0, deltaF = 0;

            while (isRunning) {

                long now = System.nanoTime();
                deltaU += (now - lastTime) / timeU;
                deltaF += (now - lastTime) / timeF;
                lastTime = now;

                if (deltaU >= 1) {
                    trigger("update");
                    ticks++;
                    deltaU--;
                }
                if (deltaF >= 1) {
                    trigger("render");
                    frames++;
                    deltaF--;
                }

                if (System.currentTimeMillis() - timer > 1000) { // print information
                    timer += 1000;
                    seconds++;
                    store().write((writable) -> {
                        writable.putComponent(0, new LoopInformation(frames, ticks));
                    });
                    trigger("clock:1sec");
                    frames = 0;
                    ticks = 0;
                }

                if (seconds == 60) {
                    min++;
                    seconds = 0;
                    trigger("clock:1min");
                }
            }
}

}
