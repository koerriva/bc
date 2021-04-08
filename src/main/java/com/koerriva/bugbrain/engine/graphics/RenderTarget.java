package com.koerriva.bugbrain.engine.graphics;

import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import com.koerriva.bugbrain.engine.graphics.g2d.SpriteRenderer;
import com.koerriva.bugbrain.engine.graphics.raytrcing.RaytracingRenderer;
import com.koerriva.bugbrain.engine.scene.GameObject;
import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Vector2f;

import java.util.ArrayList;

public class RenderTarget extends GameObject {
    private final RenderTexture renderTexture;
    private final Transform worldTransform;

    private final ArrayList<GameObject> renderList = new ArrayList<>();

    public RenderTarget(Vector2f position, Vector2f size,RenderTexture renderTexture) {
        super(position, size, Material.from(renderTexture));
        this.renderTexture = renderTexture;
        this.worldTransform = new Transform();
    }

    public RenderTarget(Vector2f position, Vector2f size,RenderTexture renderTexture,boolean viewport) {
        super(position, size, Material.from(renderTexture,Shader.load(viewport?"hud":"base")));
        this.renderTexture = renderTexture;
        this.worldTransform = new Transform();
    }

    @Override
    public Transform getWorldTransform() {
        this.worldTransform.setTranslation(position);
        this.worldTransform.setScaling(size);
        return worldTransform;
    }

    @Override
    public void input(Window window) {

    }

    @Override
    public void update(float deltaTime) {

    }

    public void add(GameObject gameObject){
        renderList.add(gameObject);
    }

    public void draw(SpriteRenderer renderer){
        renderTexture.bind();
        renderer.newFrame(renderTexture.width,renderTexture.height);

        for (GameObject gameobject : renderList) {
            renderer.draw(gameobject);
        }

        renderTexture.unbind();
    }

    public void draw(RaytracingRenderer renderer){
        renderer.newFrame(renderTexture.getTexture());
        renderer.draw();
    }

    @Override
    public void draw(Camera2D camera) {
        material.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setModel(getWorldTransform().getWorldMatrix())
                .setColor()
                .setTexture();
        mesh.draw();
    }

    @Override
    public void cleanup() {

    }
}
