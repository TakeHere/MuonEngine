package core.shaders;

import core.objects.entities.Camera;
import core.toolbox.Maths;
import org.joml.Matrix4f;

public class SkyboxShader extends ShaderProgram{

    private static final String VERTEX_FILE = "res/shaders/skyboxVertex.vs";
    private static final String FRAGMENT_FILE = "res/shaders/skyboxFragment.fs";

    private int location_projectionMatrix;
    private int location_viewMatrix;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix4f){
        super.loadMatrix(location_projectionMatrix, matrix4f);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = camera.getViewMatrix();
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);

        super.loadMatrix(location_viewMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
