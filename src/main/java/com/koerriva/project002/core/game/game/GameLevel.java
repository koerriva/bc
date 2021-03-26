package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.game.brain.Brain;
import com.koerriva.project002.core.game.game.brain.Muscle;
import com.koerriva.project002.core.game.game.brain.Neural;
import com.koerriva.project002.core.game.game.brain.Vision;
import com.koerriva.project002.core.game.graphic.*;
import org.joml.Vector2f;

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

//        Vision vision1 = new Vision(new Vector2f(0f,0f),new Vector2f(32));
//        brain.add(vision1);
//        Vision vision2 = new Vision(new Vector2f(0f,64f),new Vector2f(32));
//        brain.add(vision2);
//        random.setSeed(System.currentTimeMillis());
//        for (int i = 0; i < 1000; i++) {
//            int x = random.nextInt(1000)-1000/2;
//            int y = random.nextInt(1000)-1000/2;
//            Neural neural = new Neural(new Vector2f(x,y),new Vector2f(32));
//            brain.add(neural);
//        }

        Neural neural1 = new Neural(new Vector2f(-64f,0f),new Vector2f(32));
        Neural neural2 = new Neural(new Vector2f(64f,0f),new Vector2f(32));
        Neural neural3 = new Neural(new Vector2f(64f,2*64f),new Vector2f(32));
        brain.add(neural1);
        brain.add(neural2);
        brain.add(neural3);
//
//        Muscle muscle1 = new Muscle(new Vector2f(64*2f,0f),new Vector2f(32f));
//        brain.add(muscle1);
//        int objNum = 100_000;
//        FloatBuffer colorData = MemoryUtil.memAllocFloat(objNum * 4);
//        FloatBuffer modelData = MemoryUtil.memAllocFloat(objNum * 16);
//
//        Matrix4f transform = new Matrix4f();
//        Vector4f color = new Vector4f();
//        for (int i = 0; i < objNum; i++) {
//            int x = random.nextInt(width)-width/2;
//            int y = random.nextInt(height)-height/2;
//            transform.identity()
//                    .translate(x,y,0.0f)
//                    .rotateZ(0)
//                    .scale(texture.width,texture.height,0f);
//            transform.get(i*16,modelData);
//            color.x = random.nextFloat()*0.1f+0.1f;
//            color.y = random.nextFloat()*0.1f+0.9f;
//            color.z = random.nextFloat()*0.1f+0.1f;
//            color.get(i*4,colorData);
//        }

//        Neural neural = new Neural(Material.from(texture),objNum,colorData,modelData);
//        level.objects.add(neural);

        Texture start_texture = Texture.load("star.png");
        Particle particle = new Particle(new Vector2f(0f),new Vector2f(16),Material.from(start_texture),500);
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
