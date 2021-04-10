package com.koerriva.bugbrain.engine.graphics;

import com.koerriva.bugbrain.engine.graphics.rtx.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

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

    public static Material from(RenderTexture renderTexture){
        return new Material(Shader.load("base"),renderTexture.getTexture(),new Vector4f(1f));
    }

    public static Material from(RenderTexture renderTexture,Shader shader){
        return new Material(shader,renderTexture.getTexture(),new Vector4f(1f));
    }

    public static Material from(Shader shader, Vector2f size){
        return new Material(shader,Texture.blank(size),new Vector4f(1f));
    }

    public static Material from(Texture texture,Shader shader){
        return new Material(shader,texture,new Vector4f(1f));
    }

    public final Material use(){
        shader.use();
        setTime((float) GLFW.glfwGetTime());
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

    public final Material setTime(float time){
        shader.setFloat("time",time);
        return this;
    }

    public Material setInstance(int instance) {
        shader.setInt("isInstance",instance);
        return this;
    }

    public Material setRayCamera(RayCamera camera) {
        shader.setVec3("camera.origin",camera.getOrigin());
        shader.setVec3("camera.lowerLeftCorner",camera.getLowerLeftCorner());
        shader.setVec3("camera.horizontal", camera.getHorizontal());
        shader.setVec3("camera.vertical",camera.getVertical());
        return this;
    }

    public Material setViewport(int width,int height) {
        shader.setVec2i("viewport",new Vector2i(width,height));
        return this;
    }

    private void setMat(String name, Mat mat){
        String attr_mat_type = name+".type";
        String attr_mat_albedo = name+".albedo";
        String attr_mat_fuzz = name+".fuzz";
        String attr_mat_ref_idx = name+".ref_idx";
        shader.setInt(attr_mat_type,mat.type);
        shader.setVec3(attr_mat_albedo, mat.albedo);
        shader.setFloat(attr_mat_fuzz,mat.fuzz);
        shader.setFloat(attr_mat_ref_idx,mat.ref_idx);
    }

    private void setTriangle(String name, Triangle triangle, Mat mat){
        String attr_v0 = name+".v0";
        String attr_v1 = name+".v1";
        String attr_v2 = name+".v2";
        String attr_mat = name+".material";

        shader.setVec3(attr_v0,triangle.v0);
        shader.setVec3(attr_v1,triangle.v1);
        shader.setVec3(attr_v2,triangle.v2);
        setMat(attr_mat,mat);
    }

    private void setSphere(String name, Sphere sphere, Mat mat){
        String attr_center = name+".center";
        String attr_radius = name+".r";
        String attr_mat = name+".material";

        shader.setVec3(attr_center,sphere.center);
        shader.setFloat(attr_radius,sphere.radius);
        setMat(attr_mat,mat);
    }

    public Material setWorld(List<Model> models){
        shader.setInt("world.size",models.size());
        for (int i = 0; i < models.size(); i++) {
            String name = String.format("world.item[%d]",i);
            Model model = models.get(i);
            shader.setInt(name+".type",model.type);
            if(model.type==1){
                String sphereName = name +".sphere";
                setSphere(sphereName,model.getSphere(),model.material);
            }
            if(model.type==2){
                String triangleName = name +".triangle";
                setTriangle(triangleName,model.getTriangle(),model.material);
            }
        }
        return this;
    }
}
