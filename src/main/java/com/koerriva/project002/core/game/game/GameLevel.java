package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.graphic.Material;
import com.koerriva.project002.core.game.graphic.Neural;
import com.koerriva.project002.core.game.graphic.SpriteRenderer;
import com.koerriva.project002.core.game.graphic.Texture;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GameLevel {
    private final ArrayList<GameObject> objects = new ArrayList<>();
    private FloatBuffer colorData;
    private FloatBuffer modelData;

    public static GameLevel load(int width,int height){
        GameLevel level = new GameLevel();
        Texture texture = Texture.load("neural.png");
        Random random = new Random(System.currentTimeMillis());

        int objNum = 10_000;
        for (int i = 0; i < objNum; i++) {
            int x = random.nextInt(width)-width/2;
            int y = random.nextInt(height)-height/2;

            float r = random.nextFloat()*0.2f+0.1f;
            float g = random.nextFloat()*0.2f+0.8f;
            float b = random.nextFloat()*0.2f+0.1f;
            float a = random.nextFloat()*0.1f+0.9f;

            Neural neural = new Neural(new Vector2f(x,y),
                    new Vector2f(texture.width,texture.height),
                    Material.from(new Vector4f(r,g,b,a),texture));

            level.objects.add(neural);
        }

        level.colorData = MemoryUtil.memAllocFloat(objNum * 4);
        level.modelData = MemoryUtil.memAllocFloat(objNum * 16);
        return level;
    }

    public void draw(Window window, SpriteRenderer renderer){
        renderer.newFrame(window);
        colorData.clear();
        modelData.clear();

        AtomicInteger idx = new AtomicInteger(0);
        AtomicReference<GameObject> instanceObj = new AtomicReference<>();
        objects.forEach((obj)->{
            if(obj instanceof Neural && obj.isInstance){
                obj.material.color.get(idx.get() * 4,colorData);
                obj.transform.get(idx.get() * 16,modelData);
                idx.getAndIncrement();
                instanceObj.set(obj);
            }else{
                renderer.draw(obj);
            }
        });
        if(idx.get()>0){
            renderer.drawBatch(instanceObj.get(),idx.get()+1,colorData,modelData);
        }
    }

    public boolean isCompleted(){
        return false;
    }

    public void cleanup(){
        if(colorData!=null){
            MemoryUtil.memFree(colorData);
        }
        if(modelData!=null){
            MemoryUtil.memFree(modelData);
        }
        objects.forEach(GameObject::cleanup);
    }
}
