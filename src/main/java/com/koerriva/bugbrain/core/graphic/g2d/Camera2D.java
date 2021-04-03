package com.koerriva.bugbrain.core.graphic.g2d;

import com.koerriva.bugbrain.core.game.InputManager;
import com.koerriva.bugbrain.core.game.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera2D {
    private final Vector3f eye = new Vector3f(0f,0f,1f);
    private final Vector3f up = new Vector3f(0f,1f,0f);
    private final Vector3f center = new Vector3f(0f);
    private final Matrix4f view;
    private final Matrix4f invView = new Matrix4f();
    private final Matrix4f projection;
    private final Matrix4f invProjection= new Matrix4f();
    private final Vector3f mousePosition = new Vector3f();

    private final Window window;

    public Camera2D(Window window) {
        this.window = window;
        view = new Matrix4f().lookAt(eye,center,up);
        view.invert(invView);
        projection = new Matrix4f().identity();
        projection.invert(invProjection);
    }

    public final Matrix4f getViewMatrix() {
        return view;
    }

    public void move(float dx,float dy){
        view.translate(dx,dy,0f);
    }

    public void move(Vector2f offset){
        view.translate(offset.x,offset.y,0f);
    }

    public final Matrix4f getProjectionMatrix(){
        projection.identity()
                .ortho(-window.size.frameBufferWidth/2f,window.size.frameBufferWidth/2f
                        ,-window.size.frameBufferHeight/2f,window.size.frameBufferHeight/2f,
                        -1f,1f);
        projection.invert(invProjection);
        view.invert(invView);

        Vector2f local = InputManager.mouse.getLocal();

        float nx = 2*local.x/window.size.width - 1f;
        float ny = 1f - 2*local.y/window.size.height;

        Vector4f clip = new Vector4f(nx,ny,-1f,1f);
        Vector4f eye = invProjection.transform(clip);
        eye.z = -1f;
        eye.w = 1f;
        Vector4f pos = invView.transform(eye);
        Vector3f world = new Vector3f(pos.x,pos.y,pos.z);

        mousePosition.set(world.x,world.y, world.z);
        float dwx = world.x - InputManager.mouse.getWorld().x;
        float dwy = world.y - InputManager.mouse.getWorld().y;

        InputManager.mouse.getWorld().set(world.x,world.y);
        InputManager.mouse.getWorldOffset().set(dwx,dwy);
        return projection;
    }
}
