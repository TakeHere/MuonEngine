package core.scenes;

import core.Consts;
import core.animations.Animation;
import core.audio.AudioMaster;
import core.audio.AudioSource;
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
import core.toolbox.Maths;
import core.toolbox.Vector2;
import core.toolbox.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class BenchmarkScene extends Scene{

    private List<Light> lights = new ArrayList<>();

    private Player player;

    private MasterRenderer renderer;
    private Loader loader;

    @Override
    public void init() {
        loader = new Loader();
        camera = new Camera();
        camera.setFreecam(true);
        camera.setPosition(new Vector3(0,150,0));
        renderer = new MasterRenderer(loader);

        EntityCreator.createEntity(loader, "res/models/axis.obj", "res/textures/axis.png"
                ,new Vector3(0,140,40),new Vector3(0,0,0),new Vector3(3,3,3), "axis");

        Entity sphere = EntityCreator.createEntity(loader, "res/models/testsphere.obj", "res/textures/blue.png"
                ,new Vector3(0,300,0),new Vector3(0,0,0),new Vector3(50,50,50), "sphere");

        String scaleFormula = "abs(x/4 + cos(x*0.3) * 2) + 5";

        Animation animation = new Animation(sphere, 0,200, 0.05f,
                String.valueOf(sphere.getPosition().x),
                String.valueOf(sphere.getPosition().y),
                String.valueOf(sphere.getPosition().z),

                String.valueOf(sphere.getRotation().x),
                String.valueOf(sphere.getRotation().y),
                String.valueOf(sphere.getRotation().z),

                scaleFormula,
                scaleFormula,
                scaleFormula
        );

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AudioSource source = sphere.createAudioSource();
                source.setVolume(0.4f);
                source.play(AudioMaster.loadSound("res/sounds/break.wav"));
                source.autoDestroy(true);
            }
        }, 0, 2 * 1000);

        Entity world = EntityCreator.createEntity(loader, "res/models/world.obj", "res/textures/grass.jpg"
                ,new Vector3(0,0,0),new Vector3(0,0,0),new Vector3(100,100,100), "axis");

        player = new Player(new TexturedModel(
                OBJFileLoader.loadOBJ("res/models/cube.obj", loader),
                new ModelTexture(loader.loadTexture("res/textures/transparent.png"))),
                new Vector3(0,1,0),
                new Vector3(0,0,0),
                new Vector3(10,10,10));


        Light sun = new Light(new Vector3(1000,1000,1000), new Color(255, 255, 255), "sun");
        lights.add(sun);


        //Lamps
        EntityCreator.createEntity(loader, "res/models/lamp.obj", "res/textures/lamp.png"
                ,new Vector3(0,100,200),new Vector3(0,0,0),new Vector3(10,10,10), "lamp");

        Light lamp = new Light(new Vector3(0,100,200), new Color(255, 255, 200), "lamp");
        lights.add(lamp);
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
