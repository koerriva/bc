package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.game.GameLevel;
import com.koerriva.project002.core.game.game.GameObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;

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

    public void drawBatch(GameObject instance,int batchSize,FloatBuffer colorData,FloatBuffer modelData){
        instance.draw(camera,batchSize,colorData,modelData);
    }
}
