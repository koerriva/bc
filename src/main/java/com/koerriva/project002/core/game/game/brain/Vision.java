package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.HashMap;

public class Vision extends Cell{
    private static final float activeKeepTime = 0.08f;

    private static final Vector4f baseColor = new Vector4f(0.6f,0.05f,0.05f,1f);
    private static final Vector4f activeColor = new Vector4f(0.9f,0.1f,0.1f,1f);

    private float ttl = 0;
    private float activeFrequency;
    private float activeSignal = 0;
    private float lastActiveTime = 0;

    private HashMap<Integer, Synapse> output = new HashMap<>();

    public Vision(Vector2f position, Vector2f size,float frequency) {
        super(position, size, new Vector4f(baseColor));
        this.activeFrequency = frequency;
        this.transform.identity()
                .translate(position.x,position.y,0f).scale(size.x,size.y,0f);
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
}
