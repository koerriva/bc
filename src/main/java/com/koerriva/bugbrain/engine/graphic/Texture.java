package com.koerriva.bugbrain.engine.graphic;

import org.joml.Vector2f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.*;

public class Texture {
    private static final HashMap<String,Texture> resource = new LinkedHashMap<>();
    public final int id;
    public final int width,height,channels=4;
    private int Internal_Format = GL_RGBA;
    private int Image_Format = GL_RGBA;
    private int Wrap_S = GL_REPEAT;
    private int Wrap_T = GL_REPEAT;
    private int Filter_Min = GL_LINEAR;
    private int Filter_Max = GL_LINEAR;

    private Texture(int id, int width, int height){
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public static Texture background(Vector2f size) {
        int width = (int) size.x;
        int height = (int) size.y;
        byte[] buffer = new byte[width*height*4];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = (y*width+x)*4;

                boolean isGridLine = false;
                if(x%20==0||y%20==0){
                    buffer[idx] = (byte) (80);
                    buffer[idx+1] = (byte) (80);
                    buffer[idx+2] = (byte) (80);
                    buffer[idx+3] = (byte) (255);
                    isGridLine = true;
                }
                if(x%100==0||y%100==0){
                    buffer[idx] = (byte) (0);
                    buffer[idx+1] = (byte) (0);
                    buffer[idx+2] = (byte) (0);
                    buffer[idx+3] = (byte) (255);
                    isGridLine = true;
                }

                if(!isGridLine){
                    buffer[idx] = (byte) (26);
                    buffer[idx+1] = (byte) (59);
                    buffer[idx+2] = (byte) (50);
                    buffer[idx+3] = (byte) (255);
                }
            }
        }

        ByteBuffer data = MemoryUtil.memAlloc(width*height*4);
        data.put(buffer);
        data.flip();
        return load(width,height,data);
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D,id);
    }

    public static Texture load(int width,int height,ByteBuffer data){
        int id = glGenTextures();
        Texture texture = new Texture(id, width, height);
        // Create Texture
        glBindTexture(GL_TEXTURE_2D, texture.id);
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(GL_TEXTURE_2D, 0, texture.Internal_Format, width, height, 0, texture.Image_Format, GL_UNSIGNED_BYTE,data);
        // Set Texture wrap and filter modes
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, texture.Wrap_S);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, texture.Wrap_T);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, texture.Filter_Min);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, texture.Filter_Max);
        // Unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);

        MemoryUtil.memFree(data);
        return texture;
    }

    public static Texture load(String filename) {
        if(resource.containsKey(filename)){
            return resource.get(filename);
        }
        int[] x={0};int[] y={0};int[] ch={0};
        ByteBuffer data = STBImage.stbi_load("data/images/"+filename,x,y,ch,4);

        System.out.printf("%s width=%d,height=%d,channels=%d\n",filename,x[0],y[0],ch[0]);
        Texture texture = load(x[0],y[0],data);
//        Texture texture = background(new Vector2f(x[0],y[0]));
        resource.put(filename,texture);
        return texture;
    }

    public static void cleanup(){
        Iterator<Map.Entry<String,Texture>> iterator = resource.entrySet().iterator();
        while (iterator.hasNext()){
            Texture texture = iterator.next().getValue();
            glDeleteTextures(texture.id);
            iterator.remove();
        }
    }
}
