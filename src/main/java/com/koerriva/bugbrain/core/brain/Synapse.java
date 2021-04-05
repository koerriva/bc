package com.koerriva.bugbrain.core.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Synapse extends Cell{
    private static final Vector4f baseColor = new Vector4f(0.5f,0.5f,0.01f,1f);
    private static final Vector4f activeColor = new Vector4f(0.9f,0.9f,0.1f,1f);

    private final Neural neural;
    public Synapse(Neural neural,Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(baseColor));
        this.neural = neural;
        cells.put(this.id,this);
    }

    public void update(float deltaTime){
        if(isActive){
            color.set(activeColor);
        }else {
            color.set(baseColor);
        }
    }
}
