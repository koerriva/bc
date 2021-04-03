package com.koerriva.bugbrain.core.gui;

import com.koerriva.bugbrain.core.game.InputManager;
import com.koerriva.bugbrain.core.game.Window;
import com.koerriva.bugbrain.core.gui.nanovg.Demo;
import com.koerriva.bugbrain.core.gui.nanovg.UILayer;
import com.koerriva.bugbrain.core.gui.nanovg.NanoVGUtils;
import org.lwjgl.nanovg.NanoVGGL3;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.koerriva.bugbrain.utils.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.system.MemoryUtil.NULL;

public class NanovgGUI implements GUI {
    private static long ctx;

    public static NanoVGUtils.GPUtimer gpuTimer = new NanoVGUtils.GPUtimer();
    public static Demo.PerfGraph gpuGraph = new Demo.PerfGraph();
    public static Demo.PerfGraph fpsGraph = new Demo.PerfGraph();
    public static ResourceData data;

    private UILayer uiLayer;

    public void init(Window window){
        ctx = nvgCreate(NanoVGGL3.NVG_ANTIALIAS|NanoVGGL3.NVG_STENCIL_STROKES|NanoVGGL3.NVG_DEBUG);
        if (ctx == 0) {
            throw new RuntimeException("Could not init nanovg.");
        }
        data = new ResourceData();
        if (loadData(ctx, data) == -1) {
            throw new RuntimeException("Could not load demo data");
        }

        Demo.initGraph(gpuGraph, Demo.GRAPH_RENDER_MS, "GPU Time");
        NanoVGUtils.initGPUTimer(gpuTimer);
        Demo.initGraph(fpsGraph, Demo.GRAPH_RENDER_FPS,"FPS");

        uiLayer = new UILayer();
    }

    @Override
    public void update(Window window) {

    }

    public void render(Window window){
        NanoVGUtils.startGPUTimer(gpuTimer);

//        glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
        nvgBeginFrame(ctx,window.size.width,window.size.height,window.size.xscale);

        uiLayer.draw(ctx,window.size.width,window.size.height);

        if (gpuTimer.supported) {
            Demo.renderGraph(ctx, 5 , 5, gpuGraph);
        }
        Demo.renderGraph(ctx,5,50,fpsGraph);

        Demo.drawLabel(ctx, InputManager.mouse.toString(),5,80,100,35);

        nvgEndFrame(ctx);

        int n = NanoVGUtils.stopGPUTimer(gpuTimer, Demo.gpuTimes, 3);
        for (int i = 0; i < n; i++) {
            Demo.updateGraph(gpuGraph, Demo.gpuTimes.get(i));
        }

        Demo.updateGraph(fpsGraph,window.frameTime);

    }

    @Override
    public void cleanup() {
        data.entypo.clear();
        data.NotoBold.clear();
        data.NotoBold.clear();
        data.NotoEmojiRegular.clear();
    }

    static ByteBuffer loadResource(String resource, int bufferSize) {
        try {
            return ioResourceToByteBuffer(resource, bufferSize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resource, e);
        }
    }

    static class ResourceData {

        final ByteBuffer entypo           = loadResource("data/font/entypo.ttf", 40 * 1024);
        final ByteBuffer NotoRegular = loadResource("data/font/NotoSansSC-Regular.otf", 9 * 1024 * 1024);
        final ByteBuffer NotoBold = loadResource("data/font/NotoSansSC-Bold.otf", 9 * 1024 * 1024);
        final ByteBuffer NotoEmojiRegular = loadResource("data/font/NotoEmoji-Regular.ttf", 450 * 1024);

        int fontNormal,
                fontBold,
                fontIcons,
                fontEmoji;

        int[] images = new int[12];
    }

    public static int loadData(long vg, ResourceData data) {
        if (vg == NULL) {
            return -1;
        }

        for (int i = 0; i < 12; i++) {
            String     file = "data/images/image" + (i + 1) + ".jpg";
            ByteBuffer img  = loadResource(file, 32 * 1024);
            data.images[i] = nvgCreateImageMem(vg, 0, img);
            if (data.images[i] == 0) {
                System.err.format("Could not load %s.\n", file);
                return -1;
            }
        }

        data.fontIcons = nvgCreateFontMem(vg, "icons", data.entypo, 0);
        if (data.fontIcons == -1) {
            System.err.format("Could not add font icons.\n");
            return -1;
        }
        data.fontNormal = nvgCreateFontMem(vg, "sans", data.NotoRegular, 0);
        if (data.fontNormal == -1) {
            System.err.format("Could not add font italic.\n");
            return -1;
        }
        data.fontBold = nvgCreateFontMem(vg, "sans-bold", data.NotoBold, 0);
        if (data.fontBold == -1) {
            System.err.format("Could not add font bold.\n");
            return -1;
        }

        data.fontEmoji = nvgCreateFontMem(vg, "emoji", data.NotoEmojiRegular, 0);
        if (data.fontEmoji == -1) {
            System.err.format("Could not add font emoji.\n");
            return -1;
        }

        nvgAddFallbackFontId(vg, data.fontNormal, data.fontEmoji);
        nvgAddFallbackFontId(vg, data.fontBold, data.fontEmoji);

        return 0;
    }
}
