package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.game.brain.Synapse;
import org.joml.Vector2f;
import org.joml.Vector4f;

public enum Direction {
//    90,45,0,-45,-90,-135,-180,-225
    N,NE,E,SE,S,SW,W,NW,ZERO;
    public static Direction to(Vector2f from, Vector2f to){
        Direction[][] pos = new Direction[][]{
                {NW,N,NE},
                {W,ZERO,E},
                {SW,S,SE},
        };

        int dx=1,dy=1;
        if(from.x<to.x){
            dx=0;
            if(from.y>to.y){
                dy=0;
            }
            if(from.y<to.y){
                dy=2;
            }
        }
        if(from.x==to.x){
            dx=1;
            if(from.y>to.y){
                dy=0;
            }
            if(from.y<to.y){
                dy=2;
            }
        }
        if(from.x>to.x){
            dx=2;
            if(from.y>to.y){
                dy=0;
            }
            if(from.y<to.y){
                dy=2;
            }
        }

        System.out.printf("dx=%d,dy=%d\n",dx,dy);
        return pos[dy][dx];
    }

    public Integer getAngle(){
        return 90-ordinal()*45;
    }

    public static void main(String[] args) {
        Direction dir = Direction.to(new Vector2f(-1,0),new Vector2f(0,0));
        System.out.println(dir);
        dir = Direction.to(new Vector2f(1,0),new Vector2f(0,0));
        System.out.println(dir);
        dir = Direction.to(new Vector2f(0,1),new Vector2f(0,0));
        System.out.println(dir);
        dir = Direction.to(new Vector2f(0,-1),new Vector2f(0,0));
        System.out.println(dir);
    }
}
