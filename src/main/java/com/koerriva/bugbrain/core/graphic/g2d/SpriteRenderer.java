package com.koerriva.bugbrain.core.graphic.g2d;

import com.koerriva.bugbrain.core.game.Window;
import com.koerriva.bugbrain.core.game.GameObject;

import static org.lwjgl.opengl.GL33C.*;

public class SpriteRenderer {
    private final Camera2D camera;

    public SpriteRenderer(Camera2D camera) {
        this.camera = camera;
    }

    public void newFrame(Window window){
        glViewport(0,0,window.size.frameBufferWidth,window.size.frameBufferHeight);
        glClear(GL_COLOR_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
    }

    public void cleanup(){

    }

    public void draw(GameObject obj) {
        obj.draw(camera);
    }
}
