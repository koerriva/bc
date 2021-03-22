package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera2D {
    private final Vector3f eye = new Vector3f(0f,0f,1f);
    private final Vector3f up = new Vector3f(0f,1f,0f);
    private final Vector3f center = new Vector3f(0f);
    private final Matrix4f view;
    private final Matrix4f projection;

    private final Window window;

    public Camera2D(Window window) {
        this.window = window;
        view = new Matrix4f().lookAt(eye,center,up);
        projection = new Matrix4f().identity();
    }

    public final Matrix4f getViewMatrix() {
        return view;
    }

    public void move(float dx,float dy){
        view.translate(dx,dy,0f);
    }

    public final Matrix4f getProjectionMatrix(){
        return projection.identity()
                .ortho(-window.size.frameBufferWidth/2f,window.size.frameBufferWidth/2f
                        ,-window.size.frameBufferHeight/2f,window.size.frameBufferHeight/2f,-1f,1f);
    }
}
