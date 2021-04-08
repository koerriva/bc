package com.koerriva.bugbrain.engine.graphics.raytrcing;

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
        float b = oc.dot(ray.getDirection());
        float c = oc.dot(oc) - radius*radius;
        float discriminant = b*b - a*c;
        if(discriminant>0){
            float tmp = (-b-Math.sqrt(discriminant))/a;
            if(tmp<max_t&&tmp>min_t){
                hitInfo.hit = true;
                hitInfo.t = tmp;
                hitInfo.point = ray.pointAt(hitInfo.t);
                hitInfo.normal = new Vector3f();
                hitInfo.point.sub(center,hitInfo.normal).div(radius);
                return hitInfo;
            }
            tmp = (-b+Math.sqrt(discriminant))/a;
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
}
