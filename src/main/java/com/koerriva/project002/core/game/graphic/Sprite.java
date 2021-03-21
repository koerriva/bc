package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.graphic.Shader;
import com.koerriva.project002.core.game.graphic.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Sprite {
    private final Vector2f center;
    private final Vector2f size;
    private float rotate = 0.0f;
    private final Vector4f color;
    private final Matrix4f matrix = new Matrix4f().identity();

    private final Texture texture;

    public Sprite(Vector2f center, Vector2f size, Vector4f color, Texture texture) {
        this.center = center;
        this.size = size;
        this.color = color;
        this.texture = texture;
    }

    public void render(int meshId,Shader shader){
        matrix.identity()
                .translate(center.x,center.y,0.0f)
                .rotate(rotate,0f,0f,1f)
                .scale(size.x,size.y,0f);
        shader.setMat4("M",matrix);
        shader.setVec4("baseColor",color);
        shader.setInt("baseTexture",0);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        glBindVertexArray(meshId);
        glDrawArrays(GL_TRIANGLES,0,6);
        glBindVertexArray(0);
    }

    public void render(Shader shader){
        matrix.identity()
                .translate(center.x,center.y,0.0f)
                .rotate(rotate,0f,0f,1f)
                .scale(size.x,size.y,0f);
        shader.setMat4("M",matrix);
        shader.setVec4("baseColor",color);
        shader.setInt("baseTexture",0);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        glDrawArrays(GL_TRIANGLES,0,6);
    }
}
