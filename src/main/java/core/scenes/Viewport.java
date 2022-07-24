package core.scenes;

import core.Consts;
import core.audio.AudioMaster;
import core.objects.EntityCreator;
import core.objects.GameObject;
import core.objects.entities.Camera;
import core.objects.entities.Entity;
import core.objects.entities.Light;
import core.objects.entities.Player;
import core.objects.models.ModelTexture;
import core.objects.models.TexturedModel;
import core.objects.models.objloader.OBJFileLoader;
import core.renderers.MasterRenderer;
import core.toolbox.Loader;
import core.toolbox.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Viewport extends Scene{

    private List<Light> lights = new ArrayList<>();

    private Player player;

    public static Entity entity;

    private MasterRenderer renderer;
    private Loader loader;

    @Override
    public void init() {
        loader = new Loader();
        camera = new Camera();
        camera.setFreecam(true);
        camera.setPosition(new Vector3(0,150,0));
        renderer = new MasterRenderer(loader);


        if (Consts.DEBUG){
            EntityCreator.createEntity(loader, "res/models/axis.obj", "res/textures/axis.png"
                    ,new Vector3(0,140,40),new Vector3(0,0,0),new Vector3(3,3,3), "axis");
        }

        entity = EntityCreator.createEntity(loader, "res/models/world.obj", "res/textures/grass.jpg"
                ,new Vector3(0,0,0),new Vector3(0,0,0),new Vector3(100,100,100), "axis");

        player = new Player(new TexturedModel(
                OBJFileLoader.loadOBJ("res/models/cube.obj", loader),
                new ModelTexture(loader.loadTexture("res/textures/transparent.png"))),
                new Vector3(0,1,0),
                new Vector3(0,0,0),
                new Vector3(10,10,10));


        Light sun = new Light(new Vector3(1000,1000,1000), new Color(255, 255, 255), "sun");
        lights.add(sun);
    }

    @Override
    public void update(double dt) {
        camera.update(player);

        for (Entity entity : Entity.entities) {
            renderer.processEntity(entity);
        }

        renderer.render(lights, camera, true);
    }

    @Override
    public void cleanup() {
        loader.cleanup();
        renderer.cleanup();
        AudioMaster.deleteAllSources();

        Entity.entities.clear();
        GameObject.gameObjects.clear();
    }
}
