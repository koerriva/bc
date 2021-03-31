package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Muscle extends Cell{
    private static final Vector4f baseColor = new Vector4f(0.1f,0.1f,0.5f,1f);
    private static final Vector4f activeColor = new Vector4f(0.1f,0.1f,0.9f,1f);

    public Muscle(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(baseColor));
        this.transform.identity()
                .translate(position.x,position.y,0f).scale(size.x,size.y,0f);
        cells.put(this.id,this);
    }

    public void update(float deltaTime){
        if(isActive){
            color.set(activeColor);
        }else {
            color.set(baseColor);
        }
    }
}
