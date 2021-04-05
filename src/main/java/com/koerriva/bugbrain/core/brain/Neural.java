package com.koerriva.bugbrain.core.brain;

import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

public class Neural extends Cell{
    private static final Vector4f baseColor = new Vector4f(0.1f,0.5f,0.1f,1f);
    private static final Vector4f activeColor = new Vector4f(0.1f,0.9f,0.1f,1f);

    private final HashMap<Integer,Integer> holes = new HashMap<>();
    private final HashMap<Integer,Synapse> synapses = new HashMap<>();

    private final Transform worldTransform = new Transform();

    public Neural(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(baseColor));
        cells.put(this.id, this);
    }

    protected void useSynapse(Synapse synapse,Integer angle){
        holes.put(synapse.id,angle);
        synapses.put(angle,synapse);
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

    @Override
    public Transform getWorldTransform() {
        this.worldTransform.setTranslation(position);
        this.worldTransform.setScaling(size);
        return worldTransform;
    }
}
