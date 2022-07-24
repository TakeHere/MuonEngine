package core.scenes;

import core.objects.entities.Camera;

import java.util.Timer;

public abstract class Scene {

    public static Camera camera;
    public static Timer timer = new Timer();

    public abstract void init();

    public abstract void update(double dt);

    public abstract void cleanup();

    public Camera getCamera() {
        return camera;
    }
}
