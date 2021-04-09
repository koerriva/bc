package com.koerriva.bugbrain.engine.graphics.rtx;

import com.koerriva.bugbrain.engine.graphics.Camera;
import com.koerriva.bugbrain.engine.graphics.Window;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class RayCamera extends Camera {
    private static final double M_PI = 3.14159265358979323846;

    private Vector3f lowerLeftCorner = new Vector3f(-2f,-1f,-1f);
    private Vector3f horizontal = new Vector3f(4f,0,0);
    private Vector3f vertical = new Vector3f(0,2,0);
    private Vector3f origin = new Vector3f(0,0,0);

    private final Vector3f eye = new Vector3f(-2f,2f,1f);
    private final Vector3f up = new Vector3f(0f,1f,0f);
    private final Vector3f center = new Vector3f(0f,0,-1f);
    private float vfov = 90;
    private float aspect = 2;

    private final Matrix4f view = new Matrix4f();
    private final Matrix4f invView = new Matrix4f();
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f invProjection= new Matrix4f();
    private final Vector3f mousePosition = new Vector3f();

    private final Window window;

    public RayCamera(Window window) {
        this.window = window;
        updateTransform();
    }

    private void updateTransform(){
        this.view.identity().lookAt(eye,center,up);
        this.projection.identity();

        Vector3f u = new Vector3f(),v = new Vector3f(),w = new Vector3f();
        float theta = (float) (vfov*M_PI/180);
        float half_height = Math.tan(theta/2);
        float half_width = aspect * half_height;
        origin.set(eye);

        w.set(eye).sub(center).normalize();
        u.set(up).cross(w).normalize();
        u.mul(2*half_width,horizontal);
        v.set(w).cross(u).normalize();
        v.mul(2*half_height,vertical);

        origin.sub(u.mul(half_width),lowerLeftCorner).sub(v.mul(half_height)).sub(w);
    }

    public void setVfov(float vfov) {
        this.vfov = vfov;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
        updateTransform();
    }

    public void move(Vector3f offset){
        eye.add(offset);
        updateTransform();
    }

    public void rotate(Vector3f offset){
        center.add(offset);
        updateTransform();
    }

    public Ray getRay(float u,float v){
        Vector3f u_dir = new Vector3f();
        horizontal.mul(u,u_dir);
        Vector3f v_dir = new Vector3f();
        vertical.mul(v,v_dir);
        Vector3f dir = new Vector3f();
        dir.add(lowerLeftCorner).add(u_dir).add(v_dir);
        return new Ray(origin,dir);
    }

    @Override
    public Matrix4f getView() {
        return view;
    }

    @Override
    public Matrix4f getProjection() {
        return projection;
    }

    public Vector3f getLowerLeftCorner() {
        return lowerLeftCorner;
    }

    public Vector3f getHorizontal() {
        return horizontal;
    }

    public Vector3f getVertical() {
        return vertical;
    }

    public Vector3f getOrigin() {
        return origin;
    }
}
