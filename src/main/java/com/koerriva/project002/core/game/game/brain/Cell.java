package com.koerriva.project002.core.game.game.brain;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Cell{
    private static final AtomicInteger counter = new AtomicInteger(0);

    public final int id;
    public final Vector2f position;
    public final Vector2f size;
    public final Vector4f color;

    protected final Matrix4f transform = new Matrix4f();
    protected boolean isActive = false;

    public Cell(Vector2f position, Vector2f size, Vector4f color) {
        this.position = position;
        this.size = size;
        this.color = color;
        this.id = counter.incrementAndGet();
    }
}
