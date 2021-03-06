package com.koerriva.bugbrain.engine.graphics;

import com.koerriva.bugbrain.engine.graphics.rtx.Model;
import com.koerriva.bugbrain.engine.graphics.rtx.RayCamera;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Viewport extends RenderTarget {
    private RayCamera rayCamera;
    private boolean raytracing = false;
    private List<Model> raytracingItemList = new ArrayList<>();
    private final Texture texture;

    private float aspect;

    public Viewport(Vector2f position, Vector2f size, RenderTexture renderTexture,Shader shader) {
        super(position, size, renderTexture,shader);
        this.texture = renderTexture.getTexture();

        aspect = this.texture.getAspect();
    }

    public void openRaytracing(RayCamera rayCamera){
        this.rayCamera = rayCamera;
        raytracing = true;
    }

    public void addRaytracingModel(Model model){
        raytracingItemList.add(model);
    }

    public void setViewSize(int width,int height){
        this.size.x = width;
        this.size.y = height;

        texture.resize(width,height);
        aspect = this.texture.getAspect();
        if(raytracing){
            rayCamera.setAspect(aspect);
        }
    }

    @Override
    public void draw(Camera camera) {
        material.use()
                .setProjection(camera.getProjection())
                .setModel(getWorldTransform().getWorldMatrix())
                .setTexture();
        if(raytracing){
            material.setRayCamera(rayCamera)
                    .setViewport(texture.getWidth(),texture.getHeight())
                    .setWorld(raytracingItemList);
        }
        mesh.draw();
    }
}
