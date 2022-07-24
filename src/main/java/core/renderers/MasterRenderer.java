package core.renderers;

import core.Window;
import core.audio.AudioMaster;
import core.objects.entities.Camera;
import core.objects.entities.Entity;
import core.objects.entities.Light;
import core.objects.models.TexturedModel;
import core.renderers.debug.DebugRenderer;
import core.renderers.debug.DebugSphere;
import core.shaders.StaticShader;
import core.toolbox.Loader;
import core.toolbox.Matrixes;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class MasterRenderer {

    private static final float RED = 0.33f;
    private static final float GREEN = 0.63f;
    private static final float BLUE = 0.81f;

    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();

    private EntityRenderer entityRenderer;
    private DebugRenderer debugRenderer;
    private SkyboxRenderer skyboxRenderer;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private static List<DebugSphere> debugSpheres = new ArrayList<>();

    public MasterRenderer(Loader loader){
        enableCulling();
        projectionMatrix = Matrixes.getProjectionMatrix();

        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        debugRenderer = new DebugRenderer(shader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
    }

    public static void enableCulling(){
        GL11.glEnable(GL_CULL_FACE);
        GL11.glCullFace(GL_BLUE);
    }

    public static void disableCulling(){
        GL11.glDisable(GL_CULL_FACE);
    }

    public void render(List<Light> lights, Camera camera, boolean deleteLists){
        prepare();

        shader.start();
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLights(lights);
        camera.updateViewMatrix();
        AudioMaster.setListenerData(camera);
        AudioMaster.destroyUnusedAudio();
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        debugRenderer.render(debugSpheres);
        shader.stop();

        skyboxRenderer.render(camera);

        if (deleteLists){
            entities.clear();
            debugSpheres.clear();
        }
    }

    public void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);

        if (batch != null){
            batch.add(entity);
        }else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public static void addDebugSphere(DebugSphere debugSphere){
        debugSpheres.add(debugSphere);
    }



    public void prepare(){
        GL11.glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(RED,GREEN, BLUE, 1);
    }

    public void cleanup(){
        shader.cleanup();
        debugSpheres.clear();
    }
}
