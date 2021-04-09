package com.koerriva.bugbrain.engine.graphics.rtx;

import org.joml.Math;
import org.joml.Vector3f;

public class Sphere extends Hitable{
    public final Vector3f center;
    public final float radius;

    public Sphere(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public HitInfo hit(Ray ray, float min_t, float max_t) {
        HitInfo hitInfo = new HitInfo();
        Vector3f oc = new Vector3f(ray.getOrigin()).sub(center);
        float a = ray.getDirection().dot(ray.getDirection());
        float b = 2f*oc.dot(ray.getDirection());
        float c = oc.dot(oc) - radius*radius;
        float discriminant = b*b - 4*a*c;
        if(discriminant>0){
            float tmp = (-b-Math.sqrt(discriminant))/a*0.5f;
            if(tmp<max_t&&tmp>min_t){
                hitInfo.hit = true;
                hitInfo.t = tmp;
                hitInfo.point = ray.pointAt(hitInfo.t);
                hitInfo.normal = new Vector3f();
                hitInfo.point.sub(center,hitInfo.normal).div(radius);
                return hitInfo;
            }
            tmp = (-b+Math.sqrt(discriminant))/a*0.5f;
            if(tmp<max_t&&tmp>min_t){
                hitInfo.hit = true;
                hitInfo.t = tmp;
                hitInfo.point = ray.pointAt(hitInfo.t);
                hitInfo.normal = new Vector3f();
                hitInfo.point.sub(center,hitInfo.normal).div(radius);
                return hitInfo;
            }
        }
        hitInfo.hit = false;
        return hitInfo;
    }

    private static final Vector3f point = new Vector3f();
    public static Vector3f getUnitRandomPoint(){
        do {
            float rand1 = (float) Math.random();
            float rand2 = (float) Math.random();
            float rand3 = (float) Math.random();
             point.set(rand1,rand2,rand3).mul(2.0f).sub(1,1,1);
        }while (point.dot(point)>=1.0);
        return point;
    }

    public static double drand48() {
        long seed = System.currentTimeMillis();
        seed = (0x5DEECE66DL * seed + 0xBL) & ((1L << 48) - 1);
        return (double)seed / (1L << 48);
    }
}
