package com.koerriva.project002.core.game.graphic;

import java.nio.FloatBuffer;
import java.util.LinkedHashMap;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

public class Mesh {
    public enum Type{
        QUAD,LINE,POINT,MODEL
    }
    private static final LinkedHashMap<String,Mesh> INSTANCES = new LinkedHashMap<>();
    public final int vao;
    private final int[] vbo = new int[10];
    private final float[] vertices;
    private final float[] texCoords;
    private final int[] indices;

    private int ebo;
    private String name;

    private Mesh(String name, int vao, float[] vertices, float[] texCoords, int[] indices) {
        this.vao = vao;
        this.name = name;
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.indices = indices;
    }

    public void draw(){
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES,indices.length,GL_UNSIGNED_INT,0);
        glBindVertexArray(0);
    }

    public void drawBatch(int batchSize,FloatBuffer colors,FloatBuffer transforms){
        glBindVertexArray(vao);

        //color
        glBindBuffer(GL_ARRAY_BUFFER,vbo[2]);
        glBufferData(GL_ARRAY_BUFFER,colors,GL_DYNAMIC_DRAW);
        int start = 2;
        glEnableVertexAttribArray(start);
        glVertexAttribPointer(start,4,GL_FLOAT,false,0,0);
        glVertexAttribDivisor(start,1);

        //model martix
        glBindBuffer(GL_ARRAY_BUFFER,vbo[3]);
        glBufferData(GL_ARRAY_BUFFER,transforms,GL_DYNAMIC_DRAW);
        start = 3;
        for (int i = 0; i < 4; i++) {
            glEnableVertexAttribArray(start+i);
            glVertexAttribPointer(start+i,4,GL_FLOAT,false,64,i*16);
            glVertexAttribDivisor(start+i,1);
        }

        glDrawElementsInstanced(GL_TRIANGLES,indices.length,GL_UNSIGNED_INT,0,batchSize);

        glBindVertexArray(0);
    }

    public static Mesh QUAD(String name){
        if(INSTANCES.containsKey(name)){
            return INSTANCES.get(name);
        }
        float[] vertices = {
                // 位置
                -0.5f, 0.5f,
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,
        };
        float[] texCoords = {
                // 纹理
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
        int[] indices = {
                //索引从0开始
                0,1,2,0,2,3
        };

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        Mesh mesh = new Mesh("quad", vao, vertices, texCoords, indices);

        for (int i = 0; i < 4; i++) {
            mesh.vbo[i] = glGenBuffers();
        }

        //顶点数组
        int vertexVBO = mesh.vbo[0];
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO);
        glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,2,GL_FLOAT,false,0,0);

        //纹理坐标
        int texCoordsVBO = mesh.vbo[1];
        glBindBuffer(GL_ARRAY_BUFFER, texCoordsVBO);
        glBufferData(GL_ARRAY_BUFFER,texCoords,GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1,2,GL_FLOAT,false,0,0);

        //顶点索引
        mesh.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices,GL_STATIC_DRAW);

        glBindVertexArray(0);

        INSTANCES.put(name,mesh);
        return mesh;
    }

    public void cleanup() {
        INSTANCES.remove(name);
        glDeleteBuffers(ebo);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}
