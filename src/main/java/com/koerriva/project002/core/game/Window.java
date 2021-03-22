package com.koerriva.project002.core.game;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.WGL;
import org.lwjgl.opengl.WGLNVGPUAffinity;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Window {
    public static class Size{
        public int width;
        public int height;
        public int frameBufferWidth;
        public int frameBufferHeight;
        public float xscale;
        public float yscale;
    }

    private static long handle = 0L;
    public final Size size = new Size();
    private final String title;
    private double lastFrameTime = 0.0;
    public float frameTime = 0.0f;

    public Window(int width,int height,String title){
        size.width = width;
        size.height = height;
        size.frameBufferWidth = width;
        size.frameBufferHeight = height;
        this.title = title;
    }

    public void init(){
        boolean init = glfwInit();
        if(init){
            if (Platform.get() == Platform.MACOSX) {
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
                glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            }
            glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
            glfwWindowHint(GLFW_SAMPLES, 4);

            handle = glfwCreateWindow(size.width,size.height,title,0,0);

            glfwSetWindowSizeCallback(handle,(_handle,width,height)->{
                size.width = width;
                size.height = height;
            });

            glfwSetFramebufferSizeCallback(handle,(_handle,width,height)->{
                size.frameBufferWidth = width;
                size.frameBufferHeight = height;
            });
            glfwSetWindowContentScaleCallback(handle, (_handle, xscale, yscale) -> {
                size.xscale = xscale;
                size.yscale = yscale;
            });
            try (MemoryStack stack = stackPush()) {
                IntBuffer fw = stack.mallocInt(1);
                IntBuffer   fh = stack.mallocInt(1);
                FloatBuffer sx = stack.mallocFloat(1);
                FloatBuffer sy = stack.mallocFloat(1);

                glfwGetFramebufferSize(handle, fw, fh);
                size.frameBufferWidth = fw.get(0);
                size.frameBufferHeight = fh.get(0);

                glfwGetWindowContentScale(handle, sx, sy);
                size.xscale = sx.get(0);
                size.yscale = sy.get(0);
            }


            glfwMakeContextCurrent(handle);
            glfwSetTime(0);

            GL.createCapabilities();
            glfwSwapInterval(1);

            glViewport(0,0,size.frameBufferWidth,size.frameBufferHeight);
            glClearColor(26/255.0f,59/255.0f,50/255.0f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);

            lastFrameTime = glfwGetTime();
        }
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(handle);
    }

    public void input(){
        glfwPollEvents();
    }

    public void update(){
        double t = glfwGetTime();
        frameTime = (float) (t-lastFrameTime);
        lastFrameTime = t;
        glfwSwapBuffers(handle);
    }

    public void cleanup(){
        glfwTerminate();
    }

    public long getHandle(){
        return handle;
    }

    public float getTime() {
        return (float) glfwGetTime();
    }

    public void restoreState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public boolean isKeyPress(int key){
        return glfwGetKey(handle,key) == GLFW_PRESS;
    }

    public boolean isKeyRelease(int key){
        return glfwGetKey(handle,key) == GLFW_RELEASE;
    }

    public void mkCurrent() {
        glfwMakeContextCurrent(handle);
    }
}
