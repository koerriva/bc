package com.koerriva.bugbrain.engine.graphics.rtx;

import org.joml.Vector3f;

public class Mat {
    public final int type;
    public final Vector3f albedo;
    public final float fuzz;
    public final float ref_idx;

    private Mat(int type, Vector3f albedo, float fuzz, float ref_idx) {
        this.type = type;
        this.albedo = albedo;
        this.fuzz = fuzz;
        this.ref_idx = ref_idx;
    }

    public static Mat diffuse(Vector3f albedo){
        return new Mat(1,albedo,0,1.0f);
    }

    public static Mat metal(float fuzz){
        return new Mat(2,new Vector3f(1f),fuzz,1.0f);
    }

    public static Mat glass(float fuzz,float ref_idx){
        return new Mat(3,new Vector3f(1f),0,ref_idx);
    }
}
