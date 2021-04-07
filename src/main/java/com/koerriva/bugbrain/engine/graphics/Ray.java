package com.koerriva.bugbrain.engine.graphics;

import org.joml.Vector3f;

public class Ray {
    private final Vector3f origin;
    private final Vector3f direction;
    private final Vector3f point;

    public Ray(Vector3f origin,Vector3f direction){
        this.origin = origin;
        this.direction = direction;
        this.point = new Vector3f(origin);
    }

    public Ray(float ox,float oy,float oz,float dx,float dy,float dz){
        this.origin = new Vector3f(ox,oy,oz);
        this.direction = new Vector3f(dx,dy,dz);
        this.point = new Vector3f(ox,oy,oz);
    }

    public final Vector3f pointAt(float t){
        return origin.add(direction.mul(t,point),point);
    }

    public final Vector3f getDirection() {
        return direction;
    }

    public final Vector3f getOrigin(){
        return origin;
    }
}
