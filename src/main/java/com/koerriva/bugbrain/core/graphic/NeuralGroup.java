package com.koerriva.bugbrain.core.graphic;

import com.koerriva.bugbrain.core.game.Window;
import com.koerriva.bugbrain.core.game.GameObject;
import com.koerriva.bugbrain.core.graphic.g2d.Camera2D;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class NeuralGroup extends GameObject {
    private final int batchSize;
    private final FloatBuffer colorData;
    private final FloatBuffer modelData;

    public NeuralGroup(Material material,
                       int batchSize, FloatBuffer colorData, FloatBuffer modelData) {
        super(new Vector2f(0f),new Vector2f(material.texture.width,material.texture.height),material);
        this.isInstance = true;
        this.batchSize = batchSize;
        this.colorData = colorData;
        this.modelData = modelData;
    }

    @Override
    public void input(Window window) {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void draw(Camera2D camera) {
        material.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setInstance(this.isInstance?1:0)
                .setTexture();
        mesh.drawBatch(batchSize,colorData,modelData);
    }

    @Override
    public void cleanup() {
        MemoryUtil.memFree(colorData);
        MemoryUtil.memFree(modelData);
    }
}
