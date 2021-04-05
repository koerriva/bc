package com.koerriva.bugbrain.core.brain;

import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Muscle extends Cell{
    private static final Vector4f baseColor = new Vector4f(0.1f,0.1f,0.5f,1f);
    private static final Vector4f activeColor = new Vector4f(0.1f,0.1f,0.9f,1f);

    private final Transform worldTransform = new Transform();
    public Muscle(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(baseColor));
        cells.put(this.id,this);
    }

    public void update(float deltaTime){
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
