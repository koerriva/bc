package com.koerriva.bugbrain.engine.graphics;

import com.koerriva.bugbrain.engine.scene.GameObject;
import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class NeuralGroup extends GameObject {
    private final int batchSize;
    private final FloatBuffer colorData;
    private final FloatBuffer modelData;

    private final Transform worldTransform = new Transform();

    public NeuralGroup(Material material,
                       int batchSize, FloatBuffer colorData, FloatBuffer modelData) {
        super(new Vector2f(0f),new Vector2f(material.texture.getWidth(),material.texture.getHeight()),material);
        this.isInstance = true;
        this.batchSize = batchSize;
        this.colorData = colorData;
        this.modelData = modelData;

        this.worldTransform.setTranslation(this.position);
        this.worldTransform.setScaling(this.size);
    }

    @Override
    public Transform getWorldTransform() {
        return worldTransform;
    }

    @Override
    public void input(Window window) {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void draw(Camera camera) {
        material.use()
                .setProjection(camera.getProjection())
                .setView(camera.getView())
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
