package com.koerriva.bugbrain.engine.graphics.raytrcing;

import com.koerriva.bugbrain.engine.graphics.Window;
import org.joml.Math;
import org.joml.Vector3f;

public class RayCamera {
    private Vector3f lowerLeftCorner = new Vector3f(-2f,-1f,-1f);
    private Vector3f horizontal = new Vector3f(4f,0,0);
    private Vector3f vertical = new Vector3f(0,2,0);
    private Vector3f origin = new Vector3f(0,0,0);

    private final Window window;
    public RayCamera(Window window) {
        this.window = window;
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
}
