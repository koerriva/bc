package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.game.Scene;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class SpriteRenderer {
    private int vao;
    private int ebo;
    private int colorBuffer,modelBuffer;
    private Shader shader;
    private final Matrix4f projection = new Matrix4f().identity();

    public SpriteRenderer(Shader shader) {
        this.shader = shader;
        init(true);
    }

    private void init(boolean batch){
        // 配置 VAO/VBO
        int VBO;

        float[] vertices = {
                // 位置     // 纹理
                -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 1.0f, 0.0f
        };

        int[] indices = {
                //索引从0开始
                0,1,2,0,2,3
        };

        vao = glGenVertexArrays();
        ebo = glGenBuffers();

        if(batch){
            colorBuffer = glGenBuffers();
            modelBuffer = glGenBuffers();
        }

        glBindVertexArray(vao);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,4,GL_FLOAT,false,16,0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices,GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    private void batch(List<Sprite> spriteList){
        glBindVertexArray(vao);

        int batchSize = spriteList.size();
        float[] colorData = new float[batchSize*4];
        FloatBuffer modelData = MemoryUtil.memAllocFloat(batchSize*16);

        for (int i = 0; i < batchSize; i++) {
            Sprite sprite = spriteList.get(i);
            Vector4f color = sprite.getColor();
            colorData[i] = color.x;
            colorData[i+1] = color.y;
            colorData[i+2] = color.z;
            colorData[i+3] = color.w;

            sprite.getMatrix().get(i*16,modelData);
        }

        glBindBuffer(GL_ARRAY_BUFFER,colorBuffer);
        glBufferData(GL_ARRAY_BUFFER,colorData,GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1,4,GL_FLOAT,false,64,0);
        glVertexAttribDivisor(1,1);
        glBindBuffer(GL_ARRAY_BUFFER,0);

        glBindBuffer(GL_ARRAY_BUFFER,modelBuffer);
        glBufferData(GL_ARRAY_BUFFER,modelData,GL_DYNAMIC_DRAW);

        int start = 2;
        for (int i = 0; i < 4; i++) {
            glEnableVertexAttribArray(start+i);
            glVertexAttribPointer(start+i,4,GL_FLOAT,false,64,i*16);
            glVertexAttribDivisor(start+i,1);
        }

        glBindBuffer(GL_ARRAY_BUFFER,0);

        glBindVertexArray(0);

        MemoryUtil.memFree(modelData);
    }

    public void render(Window window, Camera2D camera, List<Sprite> spriteList,Texture texture){
        batch(spriteList);

        glViewport(0,0,window.size.frameBufferWidth,window.size.frameBufferHeight);
        glClear(GL_COLOR_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);

        shader.use();

        projection.identity()
                .ortho(-window.size.frameBufferWidth/2f,window.size.frameBufferWidth/2f
                        ,-window.size.frameBufferHeight/2f,window.size.frameBufferHeight/2f,-1f,1f);
        shader.setMat4("P",projection);

        Matrix4f view = camera.getMatrix();
        shader.setMat4("V",view);
        shader.setInt("baseTexture",0);

        glBindVertexArray(vao);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        int batchSize = spriteList.size();
        glDrawElementsInstanced(GL_TRIANGLES,6,GL_UNSIGNED_INT,0,batchSize);
        glBindVertexArray(0);
    }

    public void cleanup(){
        shader.cleanup();
        glDeleteVertexArrays(vao);
    }
}
