package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class Neural extends Cell{
    private final ArrayList<Synapse> synapses = new ArrayList<>();

    public Neural(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(0.1f,0.5f,0.1f,1.0f));
        this.transform.identity()
                .translate(position.x,position.y,0f).scale(size.x,size.y,0f);
    }
}
