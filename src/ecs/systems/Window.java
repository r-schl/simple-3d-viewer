package ecs.systems;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjglx.input.Keyboard;

import ecs.EcsSystem;
import ecs.components.Keys;
import ecs.components.LoopInformation;
import ecs.components.MouseCursor;
import ecs.components.MouseScroll;
import ecs.components.Timer;
import ecs.components.WindowInformation;
import linalib.Vec2;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class Window extends EcsSystem {

    int width;
    int height;
    String title;
    long window;

    private boolean hasResized;
    // callbacks
    private GLFWWindowSizeCallback windowSizeCallback;
    private GLFWKeyCallback keyboardCallBack;
    private GLFWCursorPosCallback mousePosCallBack;
    private GLFWMouseButtonCallback mouseCallBack;
    private GLFWScrollCallback scrollCallback;

    private int[] keys;
    private Vec2 mouse;
    private Vec2 scroll;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    @Override
    public void init() {

        keys = new int[GLFW.GLFW_KEY_LAST];
        mouse = new Vec2(0);
        scroll = new Vec2(0);

        store().write((writable) -> {
            writable.putComponent0(0, new WindowInformation(width, height));
            writable.putComponent0(0, new Keys(keys));
            writable.putComponent0(0, new MouseCursor(mouse));
            writable.putComponent0(0, new MouseScroll(mouse));
        });
        register("window:create", 10, this::onCreateWindow);
        register("update", 10, this::onUpdate);
        register("render", -1, this::onRender);
        register("stop", -1, this::onStop);
        register("clock:1sec", 0, () -> {
            store().read((readable) -> {
                LoopInformation li = readable.getComponent0(0, LoopInformation.class);
                Timer timer = readable.getComponent0(0, Timer.class);
                glfwSetWindowTitle(window, "3D Viewer   [FPS: " + li.getCurrentFPS() + " UPS: " + li.getCurrentUPS()
                        + "] " + timer.getLifeTime());
            });
        });
    }

    private void onCreateWindow() {
        this.window = createWindow(width, height, title);
        trigger("window:created");
    }

    private void onRender() {
        GLFW.glfwSwapBuffers(window);
    }

    private void onUpdate() {
        if (window != 0) {
            if (glfwWindowShouldClose(window)) {
                trigger("stop");
            }
            GLFW.glfwPollEvents();
            // resize
            if (hasResized) {
                store().write((writable) -> {
                    writable.putComponent0(0, new WindowInformation(width, height));
                });
                glViewport(0, 0, width, height);
                hasResized = false;
            }
        }
    }

    private void onStop() {
        print("stopped");
        glfwTerminate();
    }

    private long createWindow(int width, int height, String title) {
        if (!GLFW.glfwInit()) {
            new Exception("[GUI] Failed to initialize GLFW").printStackTrace();
            System.exit(-1);
        }
        // when created window is not visible
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        long window = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        if (window == 0) {
            new Exception("[GUI] Failed to create window").printStackTrace();
            System.exit(-1);
        }
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        // center window
        assert vidMode != null;
        GLFW.glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        // show window
        GLFW.glfwShowWindow(window);

        // for rendering
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        // for input processing
        setLocalCallbacks(window);
        return window;
    }

    private void setLocalCallbacks(long window) {
        windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int argWidth, int argHeight) {
                width = argWidth;
                height = argHeight;
                hasResized = true;
            }
        };
        GLFW.glfwSetWindowSizeCallback(window, windowSizeCallback);

        keyboardCallBack = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = action;
                store().write((writable) -> {
                    writable.putComponent0(0, new Keys(keys));
                });
                trigger("input key " + key + " " + action);
            }
        };
        GLFW.glfwSetKeyCallback(window, keyboardCallBack);

        mousePosCallBack = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouse = new Vec2((float) xpos, (float) ypos);
                store().write((writable) -> {
                    writable.putComponent0(0, new MouseCursor(mouse));
                });
            }
        };
        GLFW.glfwSetCursorPosCallback(window, mousePosCallBack);

        mouseCallBack = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                keys[button] = action;
                store().write((writable) -> {
                    writable.putComponent0(0, new Keys(keys));
                });
                trigger("input key");
                trigger("input key " + button + " " + action);
            }
        };
        GLFW.glfwSetMouseButtonCallback(window, mouseCallBack);

        scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scroll = new Vec2((float) xoffset, (float) yoffset);
                store().write((writable) -> {
                    writable.putComponent0(0, new MouseScroll(scroll));
                });
                trigger("input scroll");
            }
        };
        GLFW.glfwSetScrollCallback(window, scrollCallback);
    }

}
