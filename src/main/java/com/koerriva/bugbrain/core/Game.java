package com.koerriva.bugbrain.core;

import com.koerriva.bugbrain.engine.audio.AudioManager;
import com.koerriva.bugbrain.engine.graphics.RaytracingRenderer;
import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import com.koerriva.bugbrain.engine.graphics.g2d.SpriteRenderer;
import com.koerriva.bugbrain.engine.gui.GUI;
import com.koerriva.bugbrain.engine.gui.NanovgGUI;
import com.koerriva.bugbrain.engine.scene.GameLevel;
import com.koerriva.bugbrain.engine.input.InputManager;
import com.koerriva.bugbrain.engine.graphics.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private GameState state;
    private GameLevel level;
    private SpriteRenderer spriteRenderer;
    private RaytracingRenderer raytracingRenderer;
    private final Window window;
    private final GUI gui;
    private final Camera2D camera;

    public Game(int width,int height,String title) {
        this.window = new Window(width,height,title);
        this.gui = new NanovgGUI();
        this.camera = new Camera2D(window);
        this.spriteRenderer = new SpriteRenderer(camera);
        this.raytracingRenderer = new RaytracingRenderer(camera);
    }

    public void init() throws Exception {
        window.init();
        AudioManager.init();
        InputManager.init(window);
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

    private final Vector2f cameraOffset = new Vector2f();
    private void input(){
        window.input();
        level.input(window);

        cameraOffset.zero();
        if(InputManager.isKeyPress(GLFW_KEY_LEFT)){
            cameraOffset.set(-1f,0f);
        }
        if(InputManager.isKeyPress(GLFW_KEY_RIGHT)){
            cameraOffset.set(1f,0f);
        }
        if(InputManager.isKeyPress(GLFW_KEY_UP)){
            cameraOffset.set(0f,1f);
        }
        if(InputManager.isKeyPress(GLFW_KEY_DOWN)){
            cameraOffset.set(0f,-1f);
        }

        if(InputManager.isDrag()){
            cameraOffset.set(InputManager.mouse.getLocalOffset());
        }
    }

    private void update(){
        if(window.shouldClose()){
            state = GameState.EXIT;
        }
        InputManager.update(window);
        camera.move(cameraOffset);
        level.update(window);
    }

    private void render(){
        level.draw(window,spriteRenderer, raytracingRenderer);
        gui.render(window);
        window.update();
    }

    private void cleanup(){
        level.cleanup();
        gui.cleanup();
        window.cleanup();

        AudioManager.cleanup();
    }
}
