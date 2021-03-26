package com.koerriva.project002.core.game.game.brain;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Vision extends Cell{
    public Vision(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(0.6f,0f,0f,1f));
    }

    @Override
    public Matrix4f getTransform() {
        return transform.identity().translate(position.x,position.y,0f).scale(size.x,size.y,0f);
    }
}
