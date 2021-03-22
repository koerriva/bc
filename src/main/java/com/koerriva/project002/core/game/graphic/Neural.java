package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.game.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Neural extends GameObject {
    private static boolean isCleanup =false;

    public Neural(Vector2f center, Vector2f size, Material material) {
        super(center,size,material);
        this.isInstance = true;
    }

    @Override
    public void draw(Camera2D camera) {
        material.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setInstance(this.isInstance?1:0)
                .setModel(transform)
                .setColor()
                .setTexture();
        mesh.draw();
    }

    @Override
    public void draw(Camera2D camera, int batchSize, FloatBuffer colorData,FloatBuffer transformData) {
        material.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setInstance(this.isInstance?1:0)
                .setTexture();
        mesh.drawBatch(batchSize,colorData,transformData);
    }

    @Override
    public void cleanup() {
        if(!isCleanup){
            material.shader.cleanup();
            material.texture.cleanup();
            if(mesh!=null){
                mesh.cleanup();
            }
            isCleanup = true;
        }
    }
}
