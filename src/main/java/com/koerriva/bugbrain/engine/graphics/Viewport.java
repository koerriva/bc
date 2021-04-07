package com.koerriva.bugbrain.engine.graphics;

import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import org.joml.Vector2f;

public class Viewport extends RenderTarget {
    public Viewport(Vector2f position, Vector2f size, RenderTexture renderTexture) {
        super(position, size, renderTexture,true);
    }

    @Override
    public void draw(Camera2D camera) {
        material.use()
                .setProjection(camera.getProjectionMatrix())
                .setModel(getWorldTransform().getWorldMatrix())
                .setTexture();
        mesh.draw();
    }
}
