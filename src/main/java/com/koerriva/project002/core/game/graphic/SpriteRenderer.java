package com.koerriva.project002.core.game.graphic;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.game.Scene;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL33C.*;

public class SpriteRenderer {
    private int vao;
    private int ebo;
    private Shader shader;
    private final Matrix4f projection = new Matrix4f().identity();

    public SpriteRenderer(Shader shader) {
        this.shader = shader;
        init();
    }

    public void init(){
        // 配置 VAO/VBO
        int VBO;
//        float[] vertices = {
//                // 位置     // 纹理
//                -0.5f, 0.5f, 0.0f, 1.0f,
//                0.5f, -0.5f, 1.0f, 0.0f,
//                -0.5f, -0.5f, 0.0f, 0.0f,
//
//                -0.5f, 0.5f, 0.0f, 1.0f,
//                0.5f, 0.5f, 1.0f, 1.0f,
//                0.5f, -0.5f, 1.0f, 0.0f
//        };

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

    public void render(Window window, Camera2D camera, Scene scene){
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
        beginRender(window,camera);
        for (Sprite sprite : scene.getEntities()){
            sprite.render(shader);
        }
        endRender();
    }

    private void beginRender(Window window, Camera2D camera){
        glBindVertexArray(vao);
    }

    private void endRender(){
        glBindVertexArray(0);
    }

    public void cleanup(){
        shader.cleanup();
        glDeleteVertexArrays(vao);
    }
}
