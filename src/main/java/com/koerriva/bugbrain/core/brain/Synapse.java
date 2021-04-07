package com.koerriva.bugbrain.core.brain;

import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.joml.Math.PI;

public class Synapse extends Cell{
    private static final Vector4f baseColor = new Vector4f(0.5f,0.5f,0.01f,1f);
    private static final Vector4f activeColor = new Vector4f(0.9f,0.9f,0.1f,1f);

    private final Neural neural;
    private final Vector2f localPosition;

    private final Transform worldTransform = new Transform();

    public Synapse(Neural neural,Vector2f size, int angle) {
        super(new Vector2f(neural.position), size, new Vector4f(baseColor));

        this.neural = neural;
        this.localPosition = getCirclePos(neural.size.x/2,angle);
        this.position.add(this.localPosition);
        neural.useSynapse(this,angle);
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
        neural.position.add(this.localPosition,this.position);
        this.worldTransform.setTranslation(position);
        this.worldTransform.setScaling(size);
        return worldTransform;
    }
}
