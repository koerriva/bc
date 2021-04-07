package com.koerriva.bugbrain.engine.graphics;

import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import org.joml.Math;
import org.joml.RayAabIntersection;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class RaytracingRenderer {
    private static final int MAX_BUFFER_SIZE = 1920*1080*4;
    private final Camera2D camera;
    private byte[] frameBuffer = new byte[MAX_BUFFER_SIZE];
    private int bufferLength = 0;
    private int width,height;
    private Texture canvas;

    public RaytracingRenderer(Camera2D camera) {
        this.camera = camera;
    }

    public void newFrame(int width,int height){
        viewport(width,height);
    }

    public void newFrame(Texture texture){
        this.canvas = texture;
        viewport(texture.width,texture.height);
    }

    public void cleanup(){

    }

    public void draw() {
        drawBackground();
        ByteBuffer data = MemoryUtil.memAlloc(width*height*4);
        data.put(frameBuffer,0,bufferLength);
        data.flip();
        canvas.swapBuffers(data);
        MemoryUtil.memFree(data);
    }

    private Vector3f lowerLeftCorner = new Vector3f(-2f,-1f,-1f);
    private Vector3f horizontal = new Vector3f(4f,0,0);
    private Vector3f vertical = new Vector3f(0,2,0);
    private Vector3f origin = new Vector3f(0,0,0);
    private void drawBackground(){
        for (int j = height-1; j >=0; j--) {
            for (int i = 0; i < width; i++) {
                float u = i*1.0f/width;
                float v = j*1.0f/height;
                Vector3f u_dir = new Vector3f();
                horizontal.mul(u,u_dir);
                Vector3f v_dir = new Vector3f();
                vertical.mul(v,v_dir);
                Vector3f dir = new Vector3f();

                dir.add(lowerLeftCorner).add(u_dir).add(v_dir);

                Ray ray = new Ray(origin,dir);
                Vector3f color = getColor(ray);

                byte ir = (byte) (255.99*color.x);
                byte ig = (byte) (255.99*color.y);
                byte ib = (byte) (255.99*color.z);

                int idx = (j*width+i)*4;

                frameBuffer[idx] = ir;
                frameBuffer[idx+1] = ig;
                frameBuffer[idx+2] = ib;
                frameBuffer[idx+3] = (byte)255;
            }
        }
    }

    private boolean hitSphere(Vector3f center,float radius,Ray ray){
        Vector3f oc = new Vector3f(ray.getOrigin()).sub(center);
        float a = ray.getDirection().dot(ray.getDirection());
        float b = 2f * oc.dot(ray.getDirection());
        float c = oc.dot(oc) - radius*radius;
        float discriminant = b*b - 4*a*c;
        return discriminant>0;
    }

    private Vector3f getColor(Ray ray){
        if(hitSphere(new Vector3f(0,0,-1),0.5f,ray)){
            return new Vector3f(1,0,0);
        }
        Vector3f unitDirection = new Vector3f(ray.getDirection()).normalize();
        float t = 0.5f*(unitDirection.y+1.0f);
        Vector3f startColor = new Vector3f(1);
        Vector3f endColor = new Vector3f(0.5f,0.7f,1f);
        return startColor.mul(1-t).add(endColor.mul(t));
//        float r = Math.lerp(1,0.5f,t);
//        float g = Math.lerp(1,0.7f,t);
//        return unitDirection.set(r,g,1f);
    }

    private void viewport(int width,int height) {
        clear();
        this.width = width;
        this.height = height;
        bufferLength = width*height*4;
    }

    private void clear(){
        for (int i=0;i<bufferLength;i++){
            frameBuffer[i]=0;
        }
    }
}
