package com.koerriva.bugbrain.engine.gui.nanovg;

import org.lwjgl.nanovg.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.OUI.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class UILayer {
    private static final UIHandler ui_root_handler = new UIHandler() {
        @Override public void invoke(int item, int event) {
            switch (event) {
                case UI_SCROLL:
                    try (MemoryStack stack = stackPush()) {
                        UIVec2 pos = uiGetScroll(UIVec2.mallocStack(stack));
                        System.out.printf("scroll! %d %d\n", pos.x(), pos.y());
                    }
                    break;
                case UI_BUTTON0_DOWN:
                    System.out.printf("%d clicks\n", uiGetClicks());
                    break;
                default:
                    break;
            }
        }
    };
    private final long uictx;
    private int peak_items;
    private int peak_alloc;
    private static final String[] menuName = new String[]{"运行","暂停","停止"};
    private static final String[] toolsetName = new String[]{"+输入","+输出","+神经元"};

    public UILayer() {
        uictx = uiCreateContext(4096, 1 << 20);
        uiMakeCurrent(uictx);
    }

    public void draw(long vg, float w, float h) {
        uiBeginLayout();

        int root = uiItem();
        // position root element
        uiSetSize(root, (int)w, (int)h);
        uiSetBox(root, UI_COLUMN);
        uiSetHandler(ui_root_handler);

        int menu = uiItem();
        uiSetLayout(menu, UI_HFILL | UI_TOP);
        uiSetBox(menu, UI_ROW);
        uiInsert(root,menu);

        int content = uiItem();
        uiSetLayout(content,UI_FILL);
        uiSetBox(content,UI_LAYOUT);
        uiInsert(root,content);

        int[] menus = new int[3];
        for (int i = 0; i < 3; i++) {
            int item = uiItem();
            uiSetSize(item,50,25);
            uiSetEvents(item,UI_BUTTON0_DOWN);
            uiSetLayout(item, UI_LEFT);
            uiSetMargins(item, 1, 1, 0, 1);
            uiInsert(menu, item);
            menus[i]=item;
        }

        int tool = uiItem();
        uiSetLayout(tool,UI_LEFT);
        uiSetBox(tool,UI_COLUMN);
        uiInsert(content,tool);

        int[] tools = new int[3];
        for (int i = 0; i < 3; i++) {
            int item = uiItem();
            uiSetSize(item,50,50);
            uiSetEvents(item,UI_BUTTON0_DOWN);
            uiSetLayout(item, UI_HFILL|UI_TOP);
            uiInsert(tool, item);
            tools[i]=item;
        }

        uiEndLayout();

        //draw
        UIRect rect = UIRect.calloc();
        for (int i = 0; i < 3; i++) {
            int item = menus[i];
            uiGetRect(item,rect);
            drawButton(vg,null,menuName[i],rect.x(),rect.y(),rect.w(),rect.h(),rgba(0, 96, 128, 255, colorA));
        }
        for (int i = 0; i < 3; i++) {
            int item = tools[i];
            uiGetRect(item,rect);
            drawButton(vg,null,toolsetName[i],rect.x(),rect.y(),rect.w(),rect.h(),rgba(0, 96, 128, 255, colorB));
        }

        uiProcess((int)(glfwGetTime() * 1000.0));

        peak_items = (peak_items > uiGetItemCount()) ? peak_items : uiGetItemCount();
        peak_alloc = (peak_alloc > uiGetAllocSize()) ? peak_alloc : uiGetAllocSize();
    }

    private static final ByteBuffer ICON_SEARCH        = cpToUTF8(0x1F50D);
    private static final ByteBuffer ICON_CIRCLED_CROSS = cpToUTF8(0x2716);
    private static final ByteBuffer ICON_CHEVRON_RIGHT = cpToUTF8(0xE75E);
    private static final ByteBuffer ICON_CHECK         = cpToUTF8(0x2713);
    private static final ByteBuffer ICON_LOGIN         = cpToUTF8(0xE740);
    private static final ByteBuffer ICON_TRASH         = cpToUTF8(0xE729);

    static final NVGColor
            colorA = NVGColor.create(),
            colorB = NVGColor.create(),
            colorC = NVGColor.create();

    static final NVGPaint
            paintA = NVGPaint.create(),
            paintB = NVGPaint.create(),
            paintC = NVGPaint.create();

    static NVGColor rgba(int r, int g, int b, int a, NVGColor color) {
        color.r(r / 255.0f);
        color.g(g / 255.0f);
        color.b(b / 255.0f);
        color.a(a / 255.0f);

        return color;
    }

    private static boolean isBlack(NVGColor col) {
        return col.r() == 0.0f && col.g() == 0.0f && col.b() == 0.0f && col.a() == 0.0f;
    }

    private static ByteBuffer cpToUTF8(int cp) {
        return memUTF8(new String(Character.toChars(cp)), false);
    }

    private static void drawCheckBox(long vg, String text, float x, float y, float w, float h) {
        NVGPaint bg = paintA;

        nvgFontSize(vg, 18.0f);
        nvgFontFace(vg, "sans");
        nvgFillColor(vg, rgba(255, 255, 255, 160, colorA));

        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        nvgText(vg, x + 28, y + h * 0.5f, text);

        nvgBoxGradient(vg, x + 1, y + (int)(h * 0.5f) - 9 + 1, 18, 18, 3, 3, rgba(0, 0, 0, 32, colorA), rgba(0, 0, 0, 92, colorB), bg);
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x + 1, y + (int)(h * 0.5f) - 9, 18, 18, 3);
        nvgFillPaint(vg, bg);
        nvgFill(vg);

        nvgFontSize(vg, 40);
        nvgFontFace(vg, "icons");
        nvgFillColor(vg, rgba(255, 255, 255, 128, colorA));
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgText(vg, x + 9 + 2, y + h * 0.5f, ICON_CHECK);
    }

    private static void drawButton(long vg, ByteBuffer preicon, String text, float x, float y, float w, float h, NVGColor col) {
        NVGPaint bg           = paintA;
        float    cornerRadius = 4.0f;
        float    tw, iw       = 0;

        nvgLinearGradient(vg, x, y, x, y + h, rgba(255, 255, 255, isBlack(col) ? 16 : 32, colorB), rgba(0, 0, 0, isBlack(col) ? 16 : 32, colorC), bg);
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x + 1, y + 1, w - 2, h - 2, cornerRadius - 1);
        if (!isBlack(col)) {
            nvgFillColor(vg, col);
            nvgFill(vg);
        }
        nvgFillPaint(vg, bg);
        nvgFill(vg);

        nvgBeginPath(vg);
        nvgRoundedRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1, cornerRadius - 0.5f);
        nvgStrokeColor(vg, rgba(0, 0, 0, 48, colorA));
        nvgStroke(vg);

        try (MemoryStack stack = stackPush()) {
            ByteBuffer textEncoded = stack.UTF8(text,false);//stack.ASCII(text, false);

            nvgFontSize(vg, 20.0f);
            nvgFontFace(vg, "sans-bold");
            tw = nvgTextBounds(vg, 0, 0, textEncoded, (FloatBuffer)null);
            if (preicon != null) {
                nvgFontSize(vg, h * 1.3f);
                nvgFontFace(vg, "icons");
                iw = nvgTextBounds(vg, 0, 0, preicon, (FloatBuffer)null);
                iw += h * 0.15f;
            }

            if (preicon != null) {
                nvgFontSize(vg, h * 1.3f);
                nvgFontFace(vg, "icons");
                nvgFillColor(vg, rgba(255, 255, 255, 96, colorA));
                nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
                nvgText(vg, x + w * 0.5f - tw * 0.5f - iw * 0.75f, y + h * 0.5f, preicon);
            }

            nvgFontSize(vg, 20.0f);
            nvgFontFace(vg, "sans-bold");
            nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
            nvgFillColor(vg, rgba(0, 0, 0, 160, colorA));
            nvgText(vg, x + w * 0.5f - tw * 0.5f + iw * 0.25f, y + h * 0.5f - 1, textEncoded);
            nvgFillColor(vg, rgba(255, 255, 255, 160, colorA));
            nvgText(vg, x + w * 0.5f - tw * 0.5f + iw * 0.25f, y + h * 0.5f, textEncoded);
        }
    }
}
