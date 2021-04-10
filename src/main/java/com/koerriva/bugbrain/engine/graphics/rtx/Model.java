package com.koerriva.bugbrain.engine.graphics.rtx;

import org.joml.Vector3f;

public class Model {
    public final Integer type;
    public final Mat material;
    private Sphere sphere;
    private Triangle triangle;

    private Model(Integer type, Mat material) {
        this.type = type;
        this.material = material;
    }

    public static Model sphere(Vector3f center, float r, Mat material){
        Model model = new Model(1,material);
        model.sphere = new Sphere(center,r);
        return model;
    }

    public static Model triangle(Vector3f v0,Vector3f v1,Vector3f v2,Mat material){
        Model model = new Model(2,material);
        model.triangle = new Triangle(v0,v1,v2);
        return model;
    }

    public Sphere getSphere() {
        return sphere;
    }

    public Triangle getTriangle() {
        return triangle;
    }
}
