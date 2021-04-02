package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.game.InputManager;
import com.koerriva.project002.core.game.game.Window;
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

    private final FloatBuffer colorData = MemoryUtil.memAllocFloat(1024*4);
    private final FloatBuffer modelData = MemoryUtil.memAllocFloat(1024*16);

    public Particle(Vector2f position, Vector2f size, Material material, int batchSize) {
        super(position, size, material);
        this.material.color.set(0.9f,0.15f,0.05f,1.0f);
        this.mesh = Mesh.QUAD("particle");
        this.life = 0.4f;
        this.isInstance = true;
        this.batchSize = batchSize;
        this.data = new ArrayList<>();

        random.setSeed(System.currentTimeMillis());
        System.out.println("init particle!");
    }

    private Data spawn(){
        Data e = new Data();
        e.life = life;
        e.color = new Vector4f(material.color);

        float xoffset = (float) Math.random()*2-1;
        float yoffset = (float) Math.random()*2-1;
        float xv = (float) (Math.random()*10f-5f);
        float yv = (float) (5f+Math.random()*5f);

        e.velocity = new Vector3f(xv,yv,0f);
        e.model = new Matrix4f().identity()
                .translate(position.x+xoffset*5,position.y+yoffset*5,0f)
                .rotateZ(rotation)
                .scale(size.x,size.y,0f);
        return e;
    }

    private void respawn(final Data e){
        float xoffset = random.nextFloat()*2-1;
        float yoffset = random.nextFloat()*2-1;
        float xv = random.nextFloat()*10f-5f;
        float yv = 5f+random.nextFloat()*5f;

        e.life = life;
        e.color.set(material.color);
        e.velocity.set(xv,yv,0f);
        e.model.identity().translate(position.x+xoffset*5,position.y+yoffset*5,0f)
                .rotateZ(rotation)
                .scale(size.x,size.y,0f);
    }

    @Override
    public void input(Window window) {
        position.set(InputManager.mouse.wx,InputManager.mouse.wy);
    }

    public void update(float deltaTime){
        if(data.size()<batchSize){
            data.add(spawn());
        }

        for (Data e : data) {
            e.life -= deltaTime;
            e.life = Math.clamp(0, life, e.life);
            e.color.w -= deltaTime*2.5f;
//            e.color.w = e.life;
            if (e.life > 0f) {
                if(e.velocity.x>0){
                    e.velocity.x -= deltaTime*200f;
                }else {
                    e.velocity.x += deltaTime*200f;
                }
                e.model.translate(e.velocity.x * deltaTime, e.velocity.y * deltaTime, e.velocity.z * deltaTime);
            }else {
                respawn(e);
            }
        }
    }

    @Override
    public void draw(Camera2D camera) {
        if(data.size()==0)return;

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
    }

    @Override
    public void cleanup() {
        MemoryUtil.memFree(colorData);
        MemoryUtil.memFree(modelData);
    }
}
