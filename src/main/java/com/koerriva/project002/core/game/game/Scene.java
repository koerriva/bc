package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.graphic.Sprite;

import java.util.ArrayList;

public class Scene {
    private final ArrayList<Sprite> entities = new ArrayList<Sprite>();

    public void add(Sprite sprite){
        entities.add(sprite);
    }

    public void remove(Sprite sprite){
        entities.remove(sprite);
    }

    public final ArrayList<Sprite> getEntities(){
        return entities;
    }
}
