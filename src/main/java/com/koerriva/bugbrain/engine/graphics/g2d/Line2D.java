package com.koerriva.bugbrain.engine.graphics.g2d;

import com.koerriva.bugbrain.engine.graphics.Window;
import com.koerriva.bugbrain.engine.scene.GameObject;
import com.koerriva.bugbrain.engine.graphics.Material;
import com.koerriva.bugbrain.engine.graphics.Texture;
import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Line2D extends GameObject {
    static class Data{
        Vector2f startPoint;
        Vector2f endPoint;
        Vector2f length;
        Vector2f position;
        Transform transform;
    }

    public static final int BEZIER_LINE_DIVISIONS = 24;
    public final float width;

    public final Vector2f startPoint;
    public final Vector2f endPoint;

    public final Vector2f[] points;

    public final ArrayList<Data> data = new ArrayList<>();

    private final FloatBuffer colors = MemoryUtil.memAllocFloat(BEZIER_LINE_DIVISIONS*4);
    private final FloatBuffer transforms = MemoryUtil.memAllocFloat(BEZIER_LINE_DIVISIONS*16);

    private final Transform worldTransform = new Transform();
    public Line2D(Vector2f startPoint, Vector2f endPoint, float width, Vector4f color) {
        super(new Vector2f(0),new Vector2f(width), Material.from(color, Texture.load("line.png")));
        this.worldTransform.setTranslation(this.position);
        this.worldTransform.setScaling(this.size);
        this.isInstance = true;
        this.width = width;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.points = getBezierPoints2D(startPoint,endPoint);

        for (int i = 0; i < points.length - 2; i++) {
            Vector2f start = points[i];
            Vector2f end = points[i+1];
            data.add(spawn(start,end,width));
        }
    }

    private Data spawn(Vector2f startPoint, Vector2f endPoint, float width){
        Data e = new Data();
        e.startPoint = new Vector2f(startPoint);
        e.endPoint = new Vector2f(endPoint);

        e.length = new Vector2f();
        e.endPoint.sub(e.startPoint,e.length);

        e.position = new Vector2f();
        e.startPoint.add(e.length.x/2,e.length.y/2,e.position);

//        System.out.println("position ="+e.position.x+"_"+e.position.y);

        Vector2f np = new Vector2f();
        e.length.normalize(np);
//        System.out.println("len normalize="+np.x+"_"+np.y);

        float cosAngle = np.dot(new Vector2f(0f,-1f));
        if(e.endPoint.x<e.startPoint.x){
            cosAngle = -cosAngle;
        }
        float angle = Math.acos(cosAngle);
//        System.out.println("angle degrees ="+Math.toDegrees(angle));

        e.transform = new Transform();
        e.transform.setTranslation(e.position);
        e.transform.setRotation(0,0,angle);
        e.transform.setScaling(new Vector2f(width,e.length.length()));
//        e.transform = new Matrix4f().identity()
//                .translate(e.position.x,e.position.y,0f)
//                .rotateZ(angle)
//                .scale(width,e.length.length(),0f);

        return e;
    }

    @Override
    public Transform getWorldTransform() {
        return worldTransform;
    }

    @Override
    public void input(Window window) {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void draw(Camera2D camera) {
        for (int i = 0; i < data.size(); i++) {
            Data e = data.get(i);
            material.color.get(i*4,colors);
            e.transform.getWorldMatrix().get(i*16,transforms);
        }

        material.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setInstance(this.isInstance?1:0)
                .setTexture();
        mesh.drawBatch(data.size(),colors,transforms);
    }

    @Override
    public void cleanup() {
        MemoryUtil.memFree(colors);
        MemoryUtil.memFree(transforms);
    }

    public static Vector2f[] getBezierPoints2D(Vector2f startPos,Vector2f endPos){
        Vector2f[] points = new Vector2f[BEZIER_LINE_DIVISIONS+1];
        Vector2f previous = new Vector2f(startPos);
        points[0]=new Vector2f(startPos);
        for (int i = 1; i <= BEZIER_LINE_DIVISIONS; i++)
        {
            // Cubic easing in-out
            // NOTE: Easing is calculated only for y position value
            Vector2f current = new Vector2f();
            current.y = EaseCubicInOut((float)i, startPos.y, endPos.y - startPos.y, (float)BEZIER_LINE_DIVISIONS);
            current.x = previous.x + (endPos.x - startPos.x)/ (float)BEZIER_LINE_DIVISIONS;

            points[i]=current;
            previous.set(current);
        }
        return points;
    }

    private static float easeInOutCubic(float x){
        return x < 0.5 ? 4 * x * x * x : (float) (1 - java.lang.Math.pow(-2 * x + 2, 3) / 2);
    }

    private static float EaseCubicInOut(float t, float b, float c, float d)
    {
        if ((t/=d/2.0f) < 1.0f) return (c/2.0f*t*t*t + b);
        t -= 2.0f; return (c/2.0f*(t*t*t + 2.0f) + b);
    }
}
