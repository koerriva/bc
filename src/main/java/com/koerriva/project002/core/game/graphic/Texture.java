package com.koerriva.project002.core.game.graphic;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11C.*;

public class Texture {
    private int id;
    public int width,height,channels=4;
    private int Internal_Format = GL_RGBA;
    private int Image_Format = GL_RGBA;
    private int Wrap_S = GL_REPEAT;
    private int Wrap_T = GL_REPEAT;
    private int Filter_Min = GL_LINEAR;
    private int Filter_Max = GL_LINEAR;

    private Texture(){}

    public void bind(){
        glBindTexture(GL_TEXTURE_2D,id);
    }

    public static Texture load(int width,int height,ByteBuffer data){
        Texture texture = new Texture();
        texture.id = glGenTextures();
        texture.width = width;
        texture.height = height;
        texture.channels = 4;

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
        int[] x={0};int[] y={0};int[] ch={0};
        ByteBuffer data = STBImage.stbi_load("data/images/"+filename,x,y,ch,4);

        System.out.printf("%s width=%d,height=%d,channels=%d\n",filename,x[0],y[0],ch[0]);
        return load(x[0],y[0],data);
    }

    public int getId() {
        return id;
    }
}
