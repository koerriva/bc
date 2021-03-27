package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.game.brain.Brain;
import com.koerriva.project002.core.game.game.brain.Muscle;
import com.koerriva.project002.core.game.game.brain.Neural;
import com.koerriva.project002.core.game.game.brain.Vision;
import com.koerriva.project002.core.game.graphic.*;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL40C;

import java.util.ArrayList;
import java.util.Random;

public class GameLevel {
    private final ArrayList<GameObject> objects = new ArrayList<>();
    private final static Random random = new Random();

    public static GameLevel load(int width,int height){
        long t = System.currentTimeMillis();
        GameLevel level = new GameLevel();
        Brain brain = new Brain(new Vector2f(0f),new Vector2f(width,height));
        System.out.println("brain load!");
        level.objects.add(brain);

        Vision vision1 = new Vision(new Vector2f(0f,0f),new Vector2f(32));
        brain.add(vision1);
        Vision vision2 = new Vision(new Vector2f(0f,-64f),new Vector2f(32));
        brain.add(vision2);

        Neural neural1 = new Neural(new Vector2f(64f,0f),new Vector2f(32));
        Neural neural2 = new Neural(new Vector2f(64f,64f),new Vector2f(32));
        Neural neural3 = new Neural(new Vector2f(64f,-64f),new Vector2f(32));
        brain.add(neural1);
        brain.add(neural2);
        brain.add(neural3);

        Muscle muscle1 = new Muscle(new Vector2f(64*2f,0f),new Vector2f(32f));
        brain.add(muscle1);

        brain.link(vision1,neural1);
        brain.link(neural1,neural2);
        brain.link(neural1,muscle1);
        brain.link(neural2,muscle1);

        Texture start_texture = Texture.load("star.png");
        Particle particle = new Particle(new Vector2f(0f),new Vector2f(16),Material.from(start_texture),50);
        level.objects.add(particle);

        t = System.currentTimeMillis()-t;
        System.out.printf("Level load![%d]ms\n",t);
        return level;
    }

    public void input(Window window){
        objects.forEach(gameObject -> gameObject.input(window));
    }

    public void update(Window window){
        objects.forEach(obj->{
            if(obj instanceof Particle){
                obj.position.set(window.mouse.wx,window.mouse.wy);
            }
            obj.update(window.frameTime);
        });
    }

    public void draw(Window window, SpriteRenderer renderer){
        renderer.newFrame(window);
        objects.forEach(renderer::draw);
    }

    public boolean isCompleted(){
        return false;
    }

    public void cleanup(){
        long t = System.currentTimeMillis();
        objects.forEach(GameObject::cleanup);

        t = System.currentTimeMillis()-t;
        System.out.printf("Level unload![%d]ms\n",t);
    }
}
