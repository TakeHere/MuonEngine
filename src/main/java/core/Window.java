package core;

import core.animations.AnimationsController;
import core.audio.AudioMaster;
import core.gui.ImGuiLayer;
import core.listeners.KeyListener;
import core.listeners.MouseListener;
import core.objects.GameObject;
import core.objects.entities.Camera;
import core.objects.entities.Entity;
import core.objects.models.ModelTexture;
import core.objects.models.RawModel;
import core.objects.models.TexturedModel;
import core.objects.models.objloader.OBJFileLoader;
import core.renderers.debug.DebugSphere;
import core.scenes.Scene;
import core.scenes.Viewport;
import core.toolbox.Loader;
import core.toolbox.RunNextFrame;
import core.toolbox.Time;
import imgui.internal.ImGui;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Timer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.windows.User32.*;

public class Window {
    private static int width = 1500;
    private static int height = 1000;
    private final String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;

    private static Window window = null;

    private static Scene currentScene = null;
    private static int currentSceneIndex = 0;

    private static boolean mouseLocked = false;

    private Window(){
        this.title = "JavaFPS";
    }

    public static void changeScene(int newScene){
        GameObject.gameObjects.clear();

        currentSceneIndex = newScene;

        if (currentScene != null){
            currentScene.cleanup();
            Scene.timer.cancel();
            Scene.timer = new Timer();
            AnimationsController.cleanup();
        }

        switch (newScene){
            case 0:
                currentScene = new Viewport();
                currentScene.init();
                break;
            default:
                assert false: "Unknown scene: " + newScene;
                break;
        }
    }

    public static Window get(){
        if (window == null){
            Window.window = new Window();
        }
        return window;
    }

    public void run() {
        System.out.println("LWJGL Version: " + Version.getVersion());

        init();
        loop();

        //Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate glfw and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        int max_width  = GetSystemMetrics(SM_CXSCREEN);
        int max_height = GetSystemMetrics(SM_CYSCREEN);

        glfwSetWindowMonitor(glfwWindow, NULL, (max_width/2)-(width/2), (max_height/2) - (height/2), width, height, GLFW_DONT_CARE);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        //OpenGL Initialization
        GL.createCapabilities();

        glfwSetWindowSizeCallback(glfwWindow, Window::windowResizeCallback);

        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        imGuiLayer.initImGui();

        AudioMaster.init();
    }

    int fps;
    long lastTime = System.currentTimeMillis();

    private void loop() {
        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        KeyListener.addPressEvent(GLFW_KEY_C, () -> {
            Camera.freecam = !Camera.freecam;
        });

        Loader loader = new Loader();

        RawModel rawModel = OBJFileLoader.loadOBJ("res/models/debugSphere.obj", loader);

        ModelTexture redTexture = new ModelTexture(loader.loadTexture("res/textures/red.png"));
        redTexture.setUseFakeLighting(true);
        ModelTexture yellowTexture = new ModelTexture(loader.loadTexture("res/textures/yellow.png"));
        yellowTexture.setUseFakeLighting(true);
        ModelTexture blueTexture = new ModelTexture(loader.loadTexture("res/textures/blue.png"));
        blueTexture.setUseFakeLighting(true);

        DebugSphere.redTexture = new TexturedModel(rawModel, redTexture);
        DebugSphere.yellowTexture = new TexturedModel(rawModel, yellowTexture);
        DebugSphere.blueTexture = new TexturedModel(rawModel, blueTexture);

        changeScene(0);

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            if (MouseListener.mouseButtonDown(0) && !ImGui.getIO().getWantCaptureMouse()){
                glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                mouseLocked = true;
            }else if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                mouseLocked = false;
            }

            if (dt >= 0){
                synchronized (Entity.entities){
                    for (Entity entity : Entity.entities) {
                        entity.updateAABB();
                        if (entity.useGravity){
                            entity.velocity.y += Consts.GRAVITY * dt;
                        }
                    }
                }

                synchronized (GameObject.gameObjects){
                    for (GameObject gameObject : GameObject.gameObjects) {
                        gameObject.addPosition(gameObject.velocity);
                        if (gameObject.getAudioSource() != null){
                            gameObject.getAudioSource().setPosition(gameObject.getPosition());
                            gameObject.getAudioSource().setVelocity(gameObject.getVelocity());
                        }
                    }
                }

                currentScene.update(dt);

                AnimationsController.playAnimations();
            }

            if (dt == 0) dt = 0.001f;
            this.imGuiLayer.update(dt, currentScene.getCamera());

            for (Runnable runnable : RunNextFrame.getRunNextFrame()) {
                runnable.run();
            }
            RunNextFrame.clear();

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;

            //--------< Display FPS >--------
            fps++;
            if (System.currentTimeMillis() - lastTime > 1000){
                glfwSetWindowTitle(glfwWindow, title + " | fps: " + fps);
                lastTime += 1000;
                fps = 0;
            }
        }

        AudioMaster.cleanUp();
        currentScene.cleanup();
        imGuiLayer.destroyImGui();
    }

    private static void windowResizeCallback(long window, int width, int height) {
        setWidth(width);
        setHeight(height);
        glViewport(0, 0, width, height);
    }

    private static void setWidth(int width) {
        Window.width = width;
    }

    private static void setHeight(int height) {
        Window.height = height;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static boolean isMouseLocked() {
        return mouseLocked;
    }
}