package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.game.GameObject;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
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
        this.material.color.set(1.0f,0.0f,0.0f,1.0f);
        this.life = 1.0f;
        this.isInstance = true;
        this.batchSize = batchSize;
        this.data = new ArrayList<>();

        System.out.println("init particle!");
    }

    private void spawn(){
        if(data.size()<batchSize){
            Data e = new Data();
            e.life = life;
            e.color = new Vector4f(material.color);
            e.velocity = new Vector3f(0f,random.nextFloat()*10f,0f);
            e.model = new Matrix4f().identity()
                    .translate(position.x+random.nextFloat()*size.x-size.x/2,position.y+random.nextFloat()*size.y-size.y/2,0f)
                    .rotateZ(rotation)
                    .scale(size.x,size.y,0f);
            data.add(e);
        }else {
            for (Data e : data) {
                if(e.life <= 0f){
                    e.life = life;
                    e.color.w = 1f;
                    e.model.identity()
                            .translate(position.x+random.nextFloat()*size.x-size.x/2,position.y+random.nextFloat()*size.y-size.y/2,0f)
                            .rotateZ(rotation)
                            .scale(size.x,size.y,0f);
                    break;
                }
            }
        }
    }

    public void update(float deltaTime){
        spawn();
        for (Data e : data) {
            e.life -= deltaTime;
            e.life = Math.clamp(0,life,e.life);
            e.color.w = e.life;
            if (e.life > 0f) {
                e.model.translate(e.velocity.x*deltaTime,e.velocity.y*deltaTime,e.velocity.z*deltaTime);
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
        material.shader.cleanup();
        material.texture.cleanup();
        if(mesh!=null){
            mesh.cleanup();
        }
    }
}
