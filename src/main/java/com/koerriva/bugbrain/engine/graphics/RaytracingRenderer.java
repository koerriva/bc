package com.koerriva.bugbrain.engine.graphics;

import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
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

    private void drawBackground(){
        for (int i = 0; i < width * height; i++) {
            frameBuffer[i*4] = (byte)255;
            frameBuffer[i*4+1] = (byte)122;
            frameBuffer[i*4+2] = (byte)255;
            frameBuffer[i*4+3] = (byte)255;
        }
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
