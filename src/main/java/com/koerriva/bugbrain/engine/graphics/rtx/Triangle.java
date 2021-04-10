package com.koerriva.bugbrain.engine.graphics.rtx;

import org.joml.Vector3f;

public class Triangle extends Hitable{
    public final Vector3f v0,v1,v2;

    public Triangle(Vector3f v0, Vector3f v1, Vector3f v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public HitInfo hit(Ray ray, float min_t, float max_t) {
        return null;
    }
}
