package ecs.systems;

import ecs.EcsSystem;
import ecs.components.LoopInformation;
import ecs.events.TestEvent;

public class Loop extends EcsSystem {

    double ups;
    double fps;
    boolean isRunning = true;
    int ticks = 0; // for information
    int frames = 0; // for information
    int seconds = 0;
    int min = 0;
    public double timeU;
    public double timeF;

    public Loop(double ups, double fps) {
        this.ups = ups;
        this.fps = fps;
        timeF = 1000000000 / fps;
        timeU = 1000000000 / ups;
    }

    @Override
    public void init() {
        System.out.println("hallo");

        registerOwnThread("loop:start", 0, this::onStartLoop);
        registerOwnThread("stop", 100, this::onStop);
    }

    private void onStop() {
        System.out.println("stttttoooop");
    }

    private void onStartLoop() {
        trigger("window:create");

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
                trigger(new TestEvent("hallo Menschen!"));
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
