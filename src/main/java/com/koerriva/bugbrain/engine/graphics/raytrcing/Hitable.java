package com.koerriva.bugbrain.engine.graphics.raytrcing;

import org.joml.Vector3f;

public abstract class Hitable {
    public static class HitInfo{
        public boolean hit = false;
        public float t=0;
        public Vector3f point = new Vector3f();
        public Vector3f normal = new Vector3f();
    }
    public abstract HitInfo hit(Ray ray,float min_t,float max_t);
}
