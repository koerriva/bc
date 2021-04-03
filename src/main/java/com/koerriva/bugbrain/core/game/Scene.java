package com.koerriva.bugbrain.core.game;

import com.koerriva.bugbrain.core.graphic.NeuralGroup;

import java.util.ArrayList;

public class Scene {
    private final ArrayList<NeuralGroup> entities = new ArrayList<NeuralGroup>();

    public void add(NeuralGroup neuralGroup){
        entities.add(neuralGroup);
    }

    public void remove(NeuralGroup neuralGroup){
        entities.remove(neuralGroup);
    }

    public final ArrayList<NeuralGroup> getEntities(){
        return entities;
    }
}
