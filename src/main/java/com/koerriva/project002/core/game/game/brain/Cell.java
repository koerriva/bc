package com.koerriva.project002.core.game.game.brain;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Cell{
    private static final AtomicInteger counter = new AtomicInteger(0);

    protected final int id;
    protected final Vector2f position;
    protected final Vector2f size;
    protected final Vector4f color;

    protected final Matrix4f transform = new Matrix4f();

    public abstract Matrix4f getTransform();

    public Cell(Vector2f position, Vector2f size, Vector4f color) {
        this.position = position;
        this.size = size;
        this.color = color;
        this.id = counter.incrementAndGet();

        System.out.println(this.id);
    }
}
