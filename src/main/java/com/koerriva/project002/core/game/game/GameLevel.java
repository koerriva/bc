package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.graphic.*;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class GameLevel {
    private final ArrayList<GameObject> objects = new ArrayList<>();

    public static GameLevel load(int width,int height){
        long t = System.currentTimeMillis();
        GameLevel level = new GameLevel();
        Texture texture = Texture.load("neural.png");
        Random random = new Random(System.currentTimeMillis());

        int objNum = 100_000;
        FloatBuffer colorData = MemoryUtil.memAllocFloat(objNum * 4);
        FloatBuffer modelData = MemoryUtil.memAllocFloat(objNum * 16);

        Matrix4f transform = new Matrix4f();
        Vector4f color = new Vector4f();
        for (int i = 0; i < objNum; i++) {
            int x = random.nextInt(width)-width/2;
            int y = random.nextInt(height)-height/2;
            transform.identity()
                    .translate(x,y,0.0f)
                    .rotateZ(0)
                    .scale(texture.width,texture.height,0f);
            transform.get(i*16,modelData);
            color.x = random.nextFloat()*0.1f+0.1f;
            color.y = random.nextFloat()*0.1f+0.9f;
            color.z = random.nextFloat()*0.1f+0.1f;
            color.get(i*4,colorData);
        }

//        Neural neural = new Neural(Material.from(texture),objNum,colorData,modelData);
//        level.objects.add(neural);

        Particle particle = new Particle(new Vector2f(0f),new Vector2f(8f),Material.from(texture),50);
        level.objects.add(particle);

        t = System.currentTimeMillis()-t;
        System.out.printf("Level load![%d]ms\n",t);
        return level;
    }

    public void update(Window window){
        objects.forEach(obj->obj.update(window.frameTime));
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
