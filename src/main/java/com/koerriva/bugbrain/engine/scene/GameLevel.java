package com.koerriva.bugbrain.engine.scene;

import com.koerriva.bugbrain.engine.graphics.*;
import com.koerriva.bugbrain.core.brain.Brain;
import com.koerriva.bugbrain.core.brain.Muscle;
import com.koerriva.bugbrain.core.brain.Neural;
import com.koerriva.bugbrain.core.brain.Vision;
import com.koerriva.bugbrain.engine.graphics.g2d.SpriteRenderer;
import com.koerriva.bugbrain.engine.graphics.rtx.Mat;
import com.koerriva.bugbrain.engine.graphics.rtx.Model;
import com.koerriva.bugbrain.engine.graphics.rtx.RaytracingRenderer;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.swing.text.View;
import java.util.ArrayList;

public class GameLevel {
    private final ArrayList<GameObject> objects = new ArrayList<>();
    private Viewport minimap;
    private Viewport realWorld;

    public static GameLevel load(int width,int height){
        long t = System.currentTimeMillis();
        GameLevel level = new GameLevel();
        Brain brain = new Brain(new Vector2f(0f),new Vector2f(width,height));
        System.out.println("brain load!");
        level.objects.add(brain);

        Vision vision1 = new Vision(new Vector2f(0f,0f),new Vector2f(32),0.2f);
        brain.add(vision1);
        Vision vision2 = new Vision(new Vector2f(0f,-64f),new Vector2f(32),2f);
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
        Particle particle = new Particle(new Vector2f(0f),new Vector2f(16), Material.from(start_texture),50);
        level.objects.add(particle);

        t = System.currentTimeMillis()-t;
        System.out.printf("Level load![%d]ms\n",t);

        level.minimap = new Viewport(new Vector2f(300f,200f),new Vector2f(100),new RenderTexture(800,600),Shader.load("hud"));
        for (GameObject o: level.objects) {
            level.minimap.add(o);
        }
        level.objects.add(level.minimap);

        level.realWorld = new Viewport(new Vector2f(0),new Vector2f(800,400),new RenderTexture(400,200),Shader.load("raytracing"));
        level.objects.add(level.realWorld);

        Model sphere1 = Model.sphere(new Vector3f(0,-1000.5f,0),1000f, Mat.diffuse(new Vector3f(0.5f,0.5f,0.5f)));
        Model sphere2 = Model.sphere(new Vector3f(0,1f,0),1f, Mat.metal(0.2f));
        Model sphere3 = Model.sphere(new Vector3f(-4f,1,0.8f),1f,Mat.diffuse(new Vector3f(0.4f,0.2f,0.1f)));
        level.realWorld.addRaytracingModel(sphere1);
        level.realWorld.addRaytracingModel(sphere2);
        level.realWorld.addRaytracingModel(sphere3);
        return level;
    }

    public void input(Window window){
        objects.forEach(gameObject -> gameObject.input(window));
    }

    public void update(Window window){
        objects.forEach(obj-> obj.update(window.frameTime));
    }

    public void draw(Window window, SpriteRenderer renderer, RaytracingRenderer raytracingRenderer){
        realWorld.setViewSize(window.size.frameBufferWidth,window.size.frameBufferHeight);
//        realWorld.draw(raytracingRenderer);
        realWorld.openRaytracing(raytracingRenderer.getCamera());
        renderer.newFrame(window.size.frameBufferWidth,window.size.frameBufferHeight);
        renderer.draw(realWorld);
//        minimap.draw(renderer);
//
//        renderer.newFrame(window.size.frameBufferWidth,window.size.frameBufferHeight);
//        objects.forEach(renderer::draw);
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
