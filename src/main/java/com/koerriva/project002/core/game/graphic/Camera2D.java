package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera2D {
    private final Vector3f eye = new Vector3f(0f,0f,1f);
    private final Vector3f up = new Vector3f(0f,1f,0f);
    private final Vector3f center = new Vector3f(0f);
    private final Matrix4f view;
    private final Matrix4f projection;
    private final Vector3f mousePosition = new Vector3f();

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
        projection.identity()
                .ortho(-window.size.frameBufferWidth/2f,window.size.frameBufferWidth/2f
                        ,-window.size.frameBufferHeight/2f,window.size.frameBufferHeight/2f,-1f,11f);

        Matrix4f invP = new Matrix4f();
        projection.get(invP);

        Matrix4f invV = new Matrix4f();
        view.get(invV);

        float nx = (float) (2*window.mouse.x/window.size.width - 1f);
        float ny = (float) (1f - 2*window.mouse.y/window.size.height);

        Vector4f clip = new Vector4f(nx,ny,-1f,1f);
        Vector4f eye = invP.invert().transform(clip);
        eye.z = -1f;
        eye.w = 0f;
        Vector4f pos = invV.invert().transform(eye);
        Vector3f world = new Vector3f(pos.x,pos.y,pos.z);


        mousePosition.set(world.x,world.y, world.z);
//        invP.mul(invV).unproject(new Vector3f((float) window.mouse.x,(float) window.mouse.y,0f),
//                new int[]{0,0,window.size.frameBufferWidth,window.size.frameBufferHeight},
//                mousePosition);

        window.mouse.wx = mousePosition.x;
        window.mouse.wy = mousePosition.y;
        return projection;
    }
}
