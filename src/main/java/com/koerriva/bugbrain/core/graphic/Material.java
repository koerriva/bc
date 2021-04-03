package com.koerriva.bugbrain.core.graphic;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

public class Material {
    public final Shader shader;
    public final Texture texture;
    public final Vector4f color;

    public Material() {
        shader = Shader.load("sprite");
        texture = Texture.load("neural.png");
        color = new Vector4f(1f);
    }

    public Material(Shader shader, Texture texture, Vector4f color) {
        this.shader = shader;
        this.texture = texture;
        this.color = color;
    }

    public Material(Vector4f color){
        this.shader = Shader.load("sprite");
        this.texture = Texture.load("neural.png");
        this.color = color;
    }

    public static Material from(Vector4f color, Texture texture) {
        return new Material(Shader.load("sprite"),texture,color);
    }

    public static Material from(Texture texture) {
        return new Material(Shader.load("sprite"),texture,new Vector4f(1f));
    }

    public final Material use(){
        shader.use();
        return this;
    }

    public final Material setProjection(Matrix4f projection){
        shader.setMat4("P",projection);
        return this;
    }

    public final Material setView(Matrix4f view){
        shader.setMat4("V",view);
        return this;
    }

    public final Material setModel(Matrix4f model){
        shader.setMat4("M",model);
        return this;
    }

    public final Material setColor(){
        shader.setVec4("color",color);
        return this;
    }

    public final Material setTexture(){
        shader.setInt("texture0",0);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        return this;
    }

    public Material setInstance(int instance) {
        shader.setInt("isInstance",instance);
        return this;
    }
}
