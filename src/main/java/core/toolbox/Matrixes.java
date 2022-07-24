package core.toolbox;

import core.Window;
import core.objects.entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static core.Consts.*;

public class Matrixes {

    private static Matrix4f projectionMatrix = createProjectionMatrix();

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale){
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(translation).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                rotateX((float)Math.toRadians(rotation.x)).
                scale(scale);

        return matrix;
    }

    public static Matrix4f updateViewMatrix(Camera camera){
        Vector3 pos = camera.getPosition();
        Vector3f rot = new Vector3f(camera.getPitch(), camera.getYaw(), camera.getRoll());
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(rot.x), new Vector3f(1,0,0))
                .rotate((float) Math.toRadians(rot.y), new Vector3f(0,1,0))
                .rotate((float) Math.toRadians(rot.z), new Vector3f(0,0,1));
        matrix.translate((float) -pos.x, (float) -pos.y, (float) -pos.z);

        return matrix;
    }

    public static Matrix4f createProjectionMatrix(){
        float aspectRatio = (float) Window.get().getWidth() / Window.get().getHeight();
        return new Matrix4f().perspective(FOV, aspectRatio,
                NEAR_PLANE, FAR_PLANE);
    }

    public static Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
