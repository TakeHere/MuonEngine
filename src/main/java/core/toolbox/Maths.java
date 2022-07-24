package core.toolbox;

import core.collision.AABB;
import core.objects.entities.Camera;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Maths {

    public static Random random = new Random();

    public Vector2 rotatePoint(float angle){
        float x = (float) Math.cos(Math.toRadians(angle));
        float y = (float) Math.sin(Math.toRadians(angle));

        Vector2 point = new Vector2(x,y);

        return point;
    }

    public static Vector3 forwardVector(Vector3 rotation){
        return new Vector3(
                (float) (Math.cos(Math.toRadians(rotation.x)) * Math.sin(Math.toRadians(rotation.y))),
                (float) -Math.sin(Math.toRadians(rotation.x)),
                (float) -(Math.cos(Math.toRadians(rotation.x)) * Math.cos(Math.toRadians(rotation.y))));
    }

    public static boolean isAabbIntersect(AABB a, AABB b){
        return (a.getPositionMin().x <= b.getPositionMax().x && a.getPositionMax().x >= b.getPositionMin().x) &&
                (a.getPositionMin().y <= b.getPositionMax().y && a.getPositionMax().y >= b.getPositionMin().y) &&
                (a.getPositionMin().z <= b.getPositionMax().z && a.getPositionMax().z >= b.getPositionMin().z);
    }

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static int randomNumberBetween(int min, int max){
        return random.nextInt(max - min + 1) + min;
    }
}
