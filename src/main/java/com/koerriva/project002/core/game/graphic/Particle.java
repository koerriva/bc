package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.game.GameObject;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static org.lwjgl.opengl.GL11C.*;

public class Particle extends GameObject {
    static class Data{
        float life;
        Vector3f velocity;
        Vector4f color;
        Matrix4f model;
    }
    private final Random random = new Random();

    private final ArrayList<Data> data;
    private final float life;
    private final int batchSize;

    public Particle(Vector2f position, Vector2f size, Material material, int batchSize) {
        super(position, size, material);
        this.material.color.set(5/255.f,39/255.f,195/255.f,1.0f);
        this.life = 0.2f;
        this.isInstance = true;
        this.batchSize = batchSize;
        this.data = new ArrayList<>();

        System.out.println("init particle!");
    }

    private Data spawn(){
        Data e = new Data();
        e.life = life;
        e.color = new Vector4f(material.color);
        e.velocity = new Vector3f(0f,5f,0f);
        e.model = new Matrix4f().identity()
                .translate(position.x+random.nextFloat()*size.x-size.x/2,position.y+random.nextFloat()*size.y-size.y/2,0f)
                .rotateZ(rotation)
                .scale(size.x,size.y,0f);
        return e;
    }

    public void update(float deltaTime){
        if(data.size()<batchSize){
            for (int i = 0; i < 5; i++) {
                data.add(spawn());
            }
        }

//        for (Data e : data) {
//            if (e.life <= 0f) {
//                e.life = life;
//                e.color.w = 1f;
//                e.model.identity()
//                        .translate(position.x + random.nextFloat() * size.x - size.x / 2, position.y + random.nextFloat() * size.y - size.y / 2, 0f)
//                        .rotateZ(rotation)
//                        .scale(size.x, size.y, 0f);
//                break;
//            }
//        }
        Iterator<Data> it = data.iterator();
        while (it.hasNext()){
            Data e = it.next();
            e.life -= deltaTime;
            e.life = Math.clamp(0,life,e.life);
            e.color.w -= deltaTime*2.5f;
            if (e.life > 0f) {
                e.model.translate(e.velocity.x*deltaTime,e.velocity.y*deltaTime,e.velocity.z*deltaTime);
            }else {
                it.remove();
            }
        }
    }

    @Override
    public void draw(Camera2D camera) {
        if(data.size()==0)return;
        FloatBuffer colorData = MemoryUtil.memAllocFloat(data.size()*4);
        FloatBuffer modelData = MemoryUtil.memAllocFloat(data.size()*16);

        for (int i = 0; i < data.size(); i++) {
            Data e = data.get(i);
            e.color.get(i*4,colorData);
            e.model.get(i*16,modelData);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        material.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setInstance(this.isInstance?1:0)
                .setTexture();
        mesh.drawBatch(data.size(),colorData,modelData);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        MemoryUtil.memFree(colorData);
        MemoryUtil.memFree(modelData);
    }

    @Override
    public void cleanup() {

    }
}
