package com.koerriva.bugbrain.core.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.LinkedHashMap;
import java.util.Optional;

public class Neural extends Cell{
    private static final Vector4f baseColor = new Vector4f(0.1f,0.5f,0.1f,1f);
    private static final Vector4f activeColor = new Vector4f(0.1f,0.9f,0.1f,1f);

    private final LinkedHashMap<Integer,Integer> holes = new LinkedHashMap<>();

    public Neural(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(baseColor));
        this.global.identity()
                .translate(position.x, position.y, 0f).scale(size.x, size.y, 0f);
        cells.put(this.id, this);
    }

    public void useSynapse(Integer synapseId,Integer angle){
        holes.put(angle,synapseId);
    }

    public Optional<Integer> getUnusedHole(){
        for (int i = 0; i < 360; i+=45) {
            if(!holes.containsKey(i))return Optional.of(i);
        }
        return Optional.empty();
    }

    public boolean isUsed(int angel) {
        return holes.containsKey(angel);
    }

    public void update(float deltaTime){
        if(isActive){
            color.set(activeColor);
        }else {
            color.set(baseColor);
        }
    }
}
