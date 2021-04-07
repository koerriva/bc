package com.koerriva.bugbrain.engine.graphics;

import static org.lwjgl.opengl.GL30C.*;

public class RenderTexture {
    private final Integer fbo;
    private final Integer texture;
    private final Integer rbo;
    public final Integer width,height;

    public RenderTexture(int width,int height){
        this.width = width;
        this.height = height;

        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,fbo);

        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,texture);
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,width,height,0,GL_RGBA,GL_UNSIGNED_INT,0);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D,0);

        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,texture,0);

        rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            throw new RuntimeException("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER,fbo);
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public Texture getTexture(){
        return Texture.create(texture,width,height);
    }
}
