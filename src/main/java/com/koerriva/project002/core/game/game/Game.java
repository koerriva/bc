package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.graphic.*;
import com.koerriva.project002.core.game.gui.GUI;
import com.koerriva.project002.core.game.gui.NanovgGUI;
import com.koerriva.project002.core.game.gui.NuklearGUI;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private GameState state;
    private Shader spriteShader;
    private Texture spriteTexture;
    private SpriteRenderer spriteRenderer;
    private Scene scene;
    private final Window window;
    private final GUI gui;
    private final Camera2D camera;

    public Game(int width,int height,String title) {
        window = new Window(width,height,title);
        gui = new NanovgGUI();
        camera = new Camera2D();
        scene = new Scene();
    }

    public void init() throws IOException {
        window.init();
        gui.init(window);

        spriteShader = Shader.load("sprite");
        spriteTexture = Texture.load("neural.png");
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 100000; i++) {
            int x = random.nextInt(10000)-5000;
            int y = random.nextInt(10000)-5000;

            float r = random.nextFloat();
            float g = random.nextFloat();
            float b = random.nextFloat();
            float a = random.nextFloat()*0.2f+0.8f;

            Sprite sprite = new Sprite(new Vector2f(x,y),
                    new Vector2f(spriteTexture.width,spriteTexture.height),
                    new Vector4f(r,g,b,a),
                    spriteTexture);
            scene.add(sprite);
        }

        this.spriteRenderer = new SpriteRenderer(spriteShader);
    }

    public void run(){
        state = GameState.ACTIVE;
        while (state!=GameState.EXIT){
            input();
            update();
            render();
        }
        cleanup();
    }

    private void input(){
        window.input();
    }

    private void update(){
        if(window.isKeyPress(GLFW_KEY_LEFT)){
            camera.move(-1f,0f);
        }
        if(window.isKeyPress(GLFW_KEY_RIGHT)){
            camera.move(1f,0f);
        }
        if(window.isKeyPress(GLFW_KEY_UP)){
            camera.move(0f,1f);
        }
        if(window.isKeyPress(GLFW_KEY_DOWN)){
            camera.move(0f,-1f);
        }

        if(window.isKeyPress(GLFW_KEY_ESCAPE)){
            state = GameState.EXIT;
        }
        if(window.shouldClose()){
            state = GameState.EXIT;
        }
    }

    private void render(){
        spriteRenderer.render(window,camera,scene.getEntities(),spriteTexture);
        gui.render(window);
        window.update();
    }

    private void cleanup(){
        spriteRenderer.cleanup();
        gui.cleanup();
        window.cleanup();
    }
}
