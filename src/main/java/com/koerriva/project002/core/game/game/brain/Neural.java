package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.LinkedHashMap;
import java.util.Optional;

public class Neural extends Cell{
    private final LinkedHashMap<Integer,Integer> holes = new LinkedHashMap<>();

    public Neural(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(0.1f,0.5f,0.1f,1.0f));
        this.transform.identity()
                .translate(position.x,position.y,0f).scale(size.x,size.y,0f);
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
}
