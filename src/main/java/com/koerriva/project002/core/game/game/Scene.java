package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.graphic.Neural;

import java.util.ArrayList;

public class Scene {
    private final ArrayList<Neural> entities = new ArrayList<Neural>();

    public void add(Neural neural){
        entities.add(neural);
    }

    public void remove(Neural neural){
        entities.remove(neural);
    }

    public final ArrayList<Neural> getEntities(){
        return entities;
    }
}
