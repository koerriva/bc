package com.koerriva.bugbrain.engine.graphics.raytrcing;

import com.koerriva.bugbrain.engine.graphics.Texture;
import org.joml.Math;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class RaytracingRenderer {
    private static final int MAX_BUFFER_SIZE = 1920*1080*4;
    private final RayCamera camera;
    private byte[] frameBuffer = new byte[MAX_BUFFER_SIZE];
    private int bufferLength = 0;
    private int width,height;
    private Texture canvas;

    private Random random = new Random();

    public RaytracingRenderer(RayCamera camera) {
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

    private boolean update = true;
    public void draw() {
//        if(update)return;
//        fill();
//        ByteBuffer data = MemoryUtil.memAlloc(width*height*4);
//        data.put(frameBuffer,0,bufferLength);
//        data.flip();
//        canvas.swapBuffers(data);
//        MemoryUtil.memFree(data);
//        update = true;
    }

    private final HitableList world = new HitableList();

    private void fill(){
        for (int i = 0; i < width * height; i++) {
            frameBuffer[i*4]=0;
            frameBuffer[i*4+1]=0;
            frameBuffer[i*4+2]=0;
            frameBuffer[i*4+3]= (byte) 255;
        }
    }

    private void trace(){
        world.clear();
        world.add(new Sphere(new Vector3f(0,0,-1),0.5f));
        world.add(new Sphere(new Vector3f(0,-100.5f,-1),100f));
        int ns = 8;

        for (int j = height-1; j >=0; j--) {
            for (int i = 0; i < width; i++) {
                Vector3f color = new Vector3f();
                for (int k = 0; k < ns; k++) {
//                    float u = (i*1.0f + random.nextFloat())/width;
//                    float v = (j*1.0f + random.nextFloat())/height;
                    float u = (i*1.0f + k*1.0f/ns)/width;
                    float v = (j*1.0f + k*1.0f/ns)/height;
//                    float u = (i*1.0f + k*random.nextFloat()/ns)/width;
//                    float v = (j*1.0f + k*random.nextFloat()/ns)/height;

                    Ray ray = camera.getRay(u,v);
                    color.add(getColor(ray,world));
                }

                color.div(ns);

                byte ir = (byte) (255.99*Math.sqrt(color.x));
                byte ig = (byte) (255.99*Math.sqrt(color.y));
                byte ib = (byte) (255.99*Math.sqrt(color.z));

                int idx = (j*width+i)*4;

                frameBuffer[idx] = ir;
                frameBuffer[idx+1] = ig;
                frameBuffer[idx+2] = ib;
                frameBuffer[idx+3] = (byte)255;
            }
        }
    }

    private Vector3f getColor(Ray ray,Hitable world){
        Hitable.HitInfo info = world.hit(ray,0f,1000f);
        if(info.hit){
            Vector3f target = new Vector3f();
            target.add(info.point).add(info.normal).add(Sphere.getUnitRandomPoint());
            return getColor(new Ray(info.point,target.sub(info.point)),world).mul(0.5f);
        }else{
            Vector3f unitDirection = new Vector3f(ray.getDirection()).normalize();
            float t = 0.5f*(unitDirection.y+1.0f);
            Vector3f startColor = new Vector3f(1);
            Vector3f endColor = new Vector3f(0.5f,0.7f,1f);
            return startColor.mul(1-t).add(endColor.mul(t));
        }
    }

    private void viewport(int width,int height) {
        clear();
        if(width==this.width&&height==this.height)return;
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
