package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Vision extends Cell{
    public Vision(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(0.6f,0.05f,0.05f,1f));
        this.transform.identity()
                .translate(position.x,position.y,0f).scale(size.x,size.y,0f);
    }
}
