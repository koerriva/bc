package com.koerriva.project002.core.game.game.brain;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Synapse extends Cell{
    private Integer form,to;

    public Synapse(Vector2f position, Vector2f size) {
        super(position, size, new Vector4f(0.5f,0.5f,0.01f,1.0f));
        this.transform.identity()
                .translate(position.x,position.y,0f).scale(size.x,size.y,0f);
    }

    public void attach(Integer to){
        this.to = to;
    }

    public void link(Integer form){
        this.form = form;
    }
}
