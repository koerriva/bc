package com.koerriva.bugbrain.engine.graphics;

import static org.lwjgl.opengl.GL30C.*;

public class RenderTexture {
    private final Integer fbo;
    private final Texture texture;
    private final Integer rbo;
    public final Integer width,height;

    public RenderTexture(int width,int height){
        this.width = width;
        this.height = height;

        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,fbo);

        texture = Texture.createRenderTexture(width,height);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,texture.id,0);

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
        return texture;
    }
}
