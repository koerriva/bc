package com.koerriva.bugbrain.core.brain;

import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Vision extends Cell{
    private static final Vector4f baseColor = new Vector4f(0.6f,0.05f,0.05f,1f);
    private static final Vector4f activeColor = new Vector4f(0.9f,0.1f,0.1f,1f);

    private final float activeFrequency;
    private float activeSignal = 0;
    private float lastActiveTime = 0;

    private final Transform worldTransform = new Transform();

    public Vision(Vector2f position, Vector2f size,float frequency) {
        super(position, size, new Vector4f(baseColor));
        this.activeFrequency = frequency;
        cells.put(this.id,this);
    }

    public void update(float deltaTime){
        ttl+=deltaTime;

        if(activeSignal>=activeFrequency){
            isActive = true;
            lastActiveTime += deltaTime;
            if(lastActiveTime>=activeKeepTime){
                isActive = false;
                activeSignal = 0;
                lastActiveTime = 0;
            }
        }else {
            activeSignal += deltaTime;
        }

        if(isActive){
            color.set(activeColor);
        }else {
            color.set(baseColor);
        }
    }

    @Override
    public Transform getWorldTransform() {
        this.worldTransform.setTranslation(position);
        this.worldTransform.setScaling(size);
        return worldTransform;
    }
}
