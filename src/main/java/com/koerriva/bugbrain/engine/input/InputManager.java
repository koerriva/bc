package com.koerriva.bugbrain.engine.input;

import com.koerriva.bugbrain.engine.graphics.Window;
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

    private static final int[] keyLastState = new int[GLFW.GLFW_KEY_LAST+1];
    private static final int[] keyCurrState = new int[GLFW.GLFW_KEY_LAST+1];

    private static final int[] mouseLastState = new int[GLFW.GLFW_MOUSE_BUTTON_LAST+1];
    private static final int[] mouseCurrState = new int[GLFW.GLFW_MOUSE_BUTTON_LAST+1];

    private static final boolean[] dragHandleState = new boolean[GLFW_MOUSE_BUTTON_LAST+1];

    public static void init(Window window){
        glfwSetMouseButtonCallback(window.getHandle(), (handle, button, action, mods) -> {
            switch (button) {
                case 1 -> button = 2;
                case 2 -> button = 1;
            }
            uiSetButton(button, mods, action == GLFW_PRESS);

            mouseLastState[button] = mouseCurrState[button];
            mouseCurrState[button] = action;
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

            keyLastState[keyCode] = keyCurrState[keyCode];
            keyCurrState[keyCode] = action;
        });
    }

    public static void update(Window window){
        mouse.resetOffset();
    }

    public static boolean isLeftPressed(){
        return mouseLastState[GLFW_MOUSE_BUTTON_LEFT]!=mouseCurrState[GLFW_MOUSE_BUTTON_LEFT] &&
                mouseCurrState[GLFW_MOUSE_BUTTON_LEFT]==GLFW_RELEASE;
    }

    public static boolean isLeftDown(){
        return mouseCurrState[GLFW_MOUSE_BUTTON_LEFT]==GLFW_PRESS;
    }

    public static boolean isLeftUp(){
        return mouseCurrState[GLFW_MOUSE_BUTTON_LEFT]==GLFW_RELEASE;
    }

    public static boolean isDrag(){
        if(dragHandleState[GLFW_MOUSE_BUTTON_LEFT])return false;
        return mouseCurrState[GLFW_MOUSE_BUTTON_LEFT]==GLFW_PRESS||mouseCurrState[GLFW_MOUSE_BUTTON_LEFT]==GLFW_REPEAT;
    }

    public static void overDrag(){
        dragHandleState[GLFW_MOUSE_BUTTON_LEFT] = true;
    }

    public static boolean isRightPressed(){
        return mouseLastState[GLFW_MOUSE_BUTTON_RIGHT]!=mouseCurrState[GLFW_MOUSE_BUTTON_RIGHT] &&
                mouseCurrState[GLFW_MOUSE_BUTTON_RIGHT]==GLFW_RELEASE;
    }

    public static boolean isRightDown(){
        return mouseCurrState[GLFW_MOUSE_BUTTON_RIGHT]==GLFW_PRESS;
    }

    public static boolean isRightUp(){
        return mouseCurrState[GLFW_MOUSE_BUTTON_RIGHT]==GLFW_RELEASE;
    }

    public static boolean isKeyPressed(int key){
        return keyLastState[key]!=keyCurrState[key] && keyCurrState[key]==GLFW_RELEASE;
    }

    public static boolean isKeyDown(int key){
        return keyCurrState[key] == GLFW_PRESS;
    }

    public static boolean isKeyUp(int key){
        return keyCurrState[key] == GLFW_RELEASE;
    }
}
