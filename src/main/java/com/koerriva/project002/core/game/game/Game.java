package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.graphic.*;
import com.koerriva.project002.core.game.gui.GUI;
import com.koerriva.project002.core.game.gui.NanovgGUI;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private GameState state;
    private GameLevel level;
    private SpriteRenderer spriteRenderer;
    private final Window window;
    private final GUI gui;
    private final Camera2D camera;

    public Game(int width,int height,String title) {
        this.window = new Window(width,height,title);
        this.gui = new NanovgGUI();
        this.camera = new Camera2D(window);
        this.spriteRenderer = new SpriteRenderer(camera);
    }

    public void init() {
        window.init();
        gui.init(window);
        this.level = GameLevel.load(10000,10000);
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
        level.input(window);
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
        level.update(window);
    }

    private void render(){
        level.draw(window,spriteRenderer);
        gui.render(window);
        window.update();
    }

    private void cleanup(){
        level.cleanup();
        gui.cleanup();
        window.cleanup();
    }
}
