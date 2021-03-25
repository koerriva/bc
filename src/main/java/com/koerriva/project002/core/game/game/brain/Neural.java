package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class Neural extends Cell{
    private final ArrayList<Synapse> synapses = new ArrayList<>();

    public Neural(Vector2f position, Vector2f size, Vector4f color) {
        super(position, size, color);
    }
}
