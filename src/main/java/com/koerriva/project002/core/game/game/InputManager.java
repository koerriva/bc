package com.koerriva.project002.core.game.game;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.OUI.*;

public class InputManager {
    public static class Mouse{
        private final Vector2f local = new Vector2f();
        private final Vector2f world = new Vector2f();
        private final Vector2f localOffset = new Vector2f();
        private final Vector2f worldOffset = new Vector2f();

        public Vector2f getLocal(){
            return local;
        }

        public Vector2f getWorld(){
            return world;
        }

        public Vector2f getLocalOffset(){
            return localOffset;
        }

        public Vector2f getWorldOffset(){
            return worldOffset;
        }

        public void resetOffset(){
            localOffset.zero();
        }

        @Override
        public String toString() {
            return "Mouse{" +
                    "x=" + local.x +
                    ", y=" + local.y +
                    ", wx=" + world.x +
                    ", wy=" + world.y +
                    ", dx=" + localOffset.x +
                    ", dy=" + localOffset.y +
                    '}';
        }
    }
    public static final Mouse mouse = new Mouse();

    private static final boolean[] keyState = new boolean[GLFW.GLFW_KEY_LAST+1];
    private static final int[] mouseState = new int[GLFW.GLFW_MOUSE_BUTTON_LAST+1];
    private static final float[] mouseStateTime = new float[GLFW.GLFW_MOUSE_BUTTON_LAST+1];
    private static final boolean[] dragHandleState = new boolean[GLFW_MOUSE_BUTTON_LAST+1];

    public static void init(Window window){
        glfwSetMouseButtonCallback(window.getHandle(), (handle, button, action, mods) -> {
            switch (button) {
                case 1:
                    button = 2;
                    break;
                case 2:
                    button = 1;
                    break;
            }
            uiSetButton(button, mods, action == GLFW_PRESS);
            mouseState[button] = action;
        });
        glfwSetCursorPosCallback(window.getHandle(), (handle, xpos, ypos) -> {
            mouse.localOffset.x = (float) (xpos-mouse.local.x);
            mouse.localOffset.y = (float) (mouse.local.y-ypos);
            mouse.local.x = (float) xpos;
            mouse.local.y = (float) ypos;
            uiSetCursor((int)xpos, (int)ypos);
        });
        glfwSetScrollCallback(window.getHandle(), (handle, xoffset, yoffset) -> uiSetScroll((int)xoffset, (int)yoffset));
        glfwSetKeyCallback(window.getHandle(), (handle, keyCode, scancode, action, mods) -> {
            if (keyCode == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(handle, true);
            }
            uiSetKey(keyCode, mods, action != GLFW_RELEASE);
            keyState[keyCode] = action != GLFW_RELEASE;
        });
    }

    public static void update(Window window){
        mouse.resetOffset();
        for (int i = 0; i < mouseState.length; i++) {
            int action = mouseState[i];
            if(action==GLFW_RELEASE){
                mouseStateTime[i]=0;
            }else {
                mouseStateTime[i] += window.frameTime;
            }
            dragHandleState[i] = false;
        }
    }

    public static boolean isKeyPress(int key){
        return keyState[key];
    }

    public static boolean isDrag(){
        if(dragHandleState[GLFW_MOUSE_BUTTON_LEFT])return false;
        return mouseState[GLFW_MOUSE_BUTTON_LEFT]==GLFW_PRESS&&mouseStateTime[GLFW_MOUSE_BUTTON_LEFT]>0.1f;
    }

    public static void dragHandled(){
        dragHandleState[GLFW_MOUSE_BUTTON_LEFT] = true;
    }
}
