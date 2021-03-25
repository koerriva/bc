package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Cell{
    private static final AtomicInteger counter = new AtomicInteger(0);

    protected final int id;
    protected final Vector2f position;
    protected final Vector2f size;
    protected final Vector4f color;

    public Cell(Vector2f position, Vector2f size, Vector4f color) {
        this.position = position;
        this.size = size;
        this.color = color;
        this.id = counter.incrementAndGet();
    }
}
