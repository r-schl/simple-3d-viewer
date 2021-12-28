package ecs.systems;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import ecs.EcsSystem;
import ecs.components.LoopInformation;
import ecs.components.Timer;
import ecs.components.WindowInformation;

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

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    @Override
    public void init() {
        store().write((writable) -> {
            writable.putComponent(0, new WindowInformation(width, height));
        });
        register("window:create", 10, this::onCreateWindow);
        register("update", 10, this::onUpdate);
        register("render", -1, this::onRender);
        register("stop", -1, this::onStop);
        register("clock:1sec", 0, () -> {
            store().read((readable) -> {
                LoopInformation li = readable.getComponent(0, LoopInformation.class);
                Timer timer = readable.getComponent(0, Timer.class);
                glfwSetWindowTitle(window, "3D Viewer   [FPS: " + li.getCurrentFPS() + " UPS: " + li.getCurrentUPS()
                        + "] " + timer.getTime());
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
                    writable.putComponent(0, new WindowInformation(width, height));
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
                // keys[key] = action;
                // root.run(Window.this::writeKeys);
                // root.trigger("key_" + key + "_" + action);
                // root.trigger("keyInput");
            }
        };
        GLFW.glfwSetKeyCallback(window, keyboardCallBack);

        mousePosCallBack = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                // mouse = new Vec2(xpos, ypos);
                // root.run(Window.this::writeMouse);
                // root.trigger("mouse_" + xpos + "_" + ypos);
                // root.trigger("mousePos");
            }
        };
        GLFW.glfwSetCursorPosCallback(window, mousePosCallBack);

        mouseCallBack = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                // keys[button] = action;
                // root.run(Window.this::writeKeys);
                // root.trigger("key_" + button + "_" + action);
                // root.trigger("mouseInput");
            }
        };
        GLFW.glfwSetMouseButtonCallback(window, mouseCallBack);

        scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                // scroll = new Vec2(xoffset, yoffset);
                // root.run(Window.this::writeScroll);

                // root.trigger("scroll_" + xoffset + "_" + yoffset);
                // root.trigger("scrollInput");
            }
        };
        GLFW.glfwSetScrollCallback(window, scrollCallback);
    }

}
