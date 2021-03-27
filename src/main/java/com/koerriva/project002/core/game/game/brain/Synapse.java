package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Synapse extends Cell{
    private static final Vector4f baseColor = new Vector4f(0.5f,0.5f,0.01f,1f);
    private static final Vector4f activeColor = new Vector4f(0.9f,0.9f,0.1f,1f);

    private Integer form,to;

    public Synapse(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(baseColor));
        this.transform.identity()
                .translate(position.x,position.y,0f).scale(size.x,size.y,0f);
    }

    public void attach(Integer to){
        this.to = to;
    }

    public void link(Integer form){
        this.form = form;
    }

    public void active(){
        isActive = true;
    }

    public void update(float deltaTime){
        if(isActive){
            color.set(activeColor);
        }else {
            color.set(baseColor);
        }
        isActive = false;
    }
}
