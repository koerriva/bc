package com.koerriva.project002.core.game.game;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.OUI.*;
import static org.lwjgl.nanovg.OUI.uiSetKey;

public class InputManager {
    public static class Mouse{
        public double x;
        public double y;
        public double wx;
        public double wy;

        private final Vector2f offset = new Vector2f();

        public Vector2f getOffset(){
            return offset;
        }

        public void resetOffset(){
            offset.zero();
        }

        @Override
        public String toString() {
            return "Mouse{" +
                    "x=" + x +
                    ", y=" + y +
                    ", wx=" + wx +
                    ", wy=" + wy +
                    ", dx=" + offset.x +
                    ", dy=" + offset.y +
                    '}';
        }
    }

    private static final boolean[] keyState = new boolean[GLFW.GLFW_KEY_LAST+1];
    private static final int[] mouseState = new int[GLFW.GLFW_MOUSE_BUTTON_LAST+1];
    private static final float[] mouseStateTime = new float[GLFW.GLFW_MOUSE_BUTTON_LAST+1];
    public static final Mouse mouse = new Mouse();

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
            mouse.offset.x = (float) (xpos-mouse.x);
            mouse.offset.y = (float) (mouse.y-ypos);
            mouse.x = xpos;
            mouse.y = ypos;
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
        }
    }

    public static boolean isKeyPress(int key){
        return keyState[key];
    }

    public static boolean isDrag(){
        return mouseState[GLFW_MOUSE_BUTTON_LEFT]==GLFW_PRESS&&mouseStateTime[GLFW_MOUSE_BUTTON_LEFT]>0.1f;
    }
}
