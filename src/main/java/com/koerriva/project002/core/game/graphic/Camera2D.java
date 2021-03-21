package com.koerriva.project002.core.game.graphic;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera2D {
    private final Vector3f eye = new Vector3f(0f,0f,1f);
    private final Vector3f up = new Vector3f(0f,1f,0f);
    private final Vector3f center = new Vector3f(0f);
    private final Matrix4f matrix;

    public Camera2D() {
        matrix = new Matrix4f().lookAt(eye,center,up);
    }

    public final Matrix4f getMatrix() {
        return matrix;
    }

    public void move(float dx,float dy){
        matrix.translate(dx,dy,0f);
    }
}
