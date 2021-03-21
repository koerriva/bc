package com.koerriva.project002.core.game.gui;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.gui.nanovg.Demo;
import com.koerriva.project002.core.game.gui.nanovg.NanoVGUtils.*;
import com.koerriva.project002.core.game.gui.nanovg.OUIState;
import org.lwjgl.nanovg.NanoVGGL3;

import static com.koerriva.project002.core.game.gui.nanovg.Demo.*;
import static com.koerriva.project002.core.game.gui.nanovg.NanoVGUtils.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.nanovg.OUI.*;
import static org.lwjgl.nanovg.OUI.uiSetKey;
import static org.lwjgl.opengl.GL11C.*;

public class NanovgGUI implements GUI {
    private static long ctx;

    public static GPUtimer gpuTimer = new GPUtimer();
    public static Demo.PerfGraph gpuGraph = new Demo.PerfGraph();
    public static Demo.PerfGraph fpsGraph = new Demo.PerfGraph();
    public static DemoData data;

    private OUIState ouiState;

    public void init(Window window){
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
        });
        glfwSetCursorPosCallback(window.getHandle(), (handle, xpos, ypos) -> uiSetCursor((int)xpos, (int)ypos));
        glfwSetScrollCallback(window.getHandle(), (handle, xoffset, yoffset) -> uiSetScroll((int)xoffset, (int)yoffset));
        glfwSetKeyCallback(window.getHandle(), (handle, keyCode, scancode, action, mods) -> {
            if (keyCode == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(handle, true);
            }
            uiSetKey(keyCode, mods, action != GLFW_RELEASE);
        });

        ctx = nvgCreate(NanoVGGL3.NVG_ANTIALIAS|NanoVGGL3.NVG_STENCIL_STROKES|NanoVGGL3.NVG_DEBUG);
        if (ctx == 0) {
            throw new RuntimeException("Could not init nanovg.");
        }
        data = new DemoData();
        if (loadDemoData(ctx, data) == -1) {
            throw new RuntimeException("Could not load demo data");
        }

        initGraph(gpuGraph, GRAPH_RENDER_MS, "GPU Time");
        initGPUTimer(gpuTimer);
        initGraph(fpsGraph,GRAPH_RENDER_FPS,"FPS");

        ouiState = new OUIState();
    }

    @Override
    public void update(Window window) {

    }

    public void render(Window window){
        startGPUTimer(gpuTimer);

//        glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
        nvgBeginFrame(ctx,window.size.width,window.size.height,window.size.xscale);

        if (gpuTimer.supported) {
            renderGraph(ctx, 5 , 5, gpuGraph);
        }

        renderGraph(ctx,5,50,fpsGraph);

        nvgEndFrame(ctx);

        int n = stopGPUTimer(gpuTimer, gpuTimes, 3);
        for (int i = 0; i < n; i++) {
            updateGraph(gpuGraph, gpuTimes.get(i));
        }

        updateGraph(fpsGraph,window.frameTime);

    }

    @Override
    public void cleanup() {

    }
}
