package com.koerriva.project002.core.game.graphic;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import static com.koerriva.project002.utils.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Shader {
    private int id;

    private Shader(){}

    public void use(){
        glUseProgram(id);
    }

    public void cleanup(){
        glDeleteProgram(id);
    }

    public void setFloat(String name,float value){
        int location = glGetUniformLocation(id,name);
        glUniform1f(location,value);
    }

    public void setInt(String name,int value){
        int location = glGetUniformLocation(id,name);
        glUniform1i(location,value);
    }

    public void setMat4(String name, Matrix4f value) {
        int location = glGetUniformLocation(id,name);
        try(MemoryStack stack= stackPush()){
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(location,false,buffer);
        }
    }

    public void setVec2(String name, Vector2f value) {
        int location = glGetUniformLocation(id,name);
        glUniform2f(location,value.x,value.y);
    }

    public void setVec3(String name, Vector3f value) {
        int location = glGetUniformLocation(id,name);
        glUniform3f(location,value.x,value.y,value.z);
    }

    public void setVec4(String name, Vector4f value) {
        int location = glGetUniformLocation(id,name);
        glUniform4f(location,value.x,value.y,value.z,value.w);
    }

    public static Shader load(String vert,String frag) {
        Shader shader = new Shader();

        int sVertex, sFragment;

        sVertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(sVertex, vert);
        glCompileShader(sVertex);

        int[] success = {0};
        glGetShaderiv(sVertex,GL_COMPILE_STATUS,success);
        if(success[0]==0){
            String log = glGetShaderInfoLog(sVertex);
            System.err.println(log);
        }

        sFragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(sFragment, frag);
        glCompileShader(sFragment);

        glGetShaderiv(sFragment,GL_COMPILE_STATUS,success);
        if(success[0]==0){
            String log = glGetShaderInfoLog(sFragment);
            System.err.println(log);
        }

        // Shader Program
        shader.id = glCreateProgram();
        glAttachShader(shader.id, sVertex);
        glAttachShader(shader.id, sFragment);

        glLinkProgram(shader.id);

        glGetShaderiv(shader.id,GL_LINK_STATUS,success);
        if(success[0]==0){
            String log = glGetProgramInfoLog(shader.id);
            System.err.println(log);
        }

        glDeleteShader(sVertex);
        glDeleteShader(sFragment);

        return shader;
    }

    public static Shader load(String name) throws IOException {
        ByteBuffer vertData = ioResourceToByteBuffer("data/shader/"+name+".vert",1024);
        ByteBuffer fragData = ioResourceToByteBuffer("data/shader/"+name+".frag",1024);

        String vert = StandardCharsets.UTF_8.decode(vertData).toString();
        String frag = StandardCharsets.UTF_8.decode(fragData).toString();
        return load(vert,frag);
    }
}
