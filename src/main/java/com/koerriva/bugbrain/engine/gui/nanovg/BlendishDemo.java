package com.koerriva.bugbrain.engine.gui.nanovg;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.nanovg.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static com.koerriva.bugbrain.utils.IOUtil.ioResourceToByteBuffer;
import static com.koerriva.bugbrain.engine.gui.nanovg.NanoVGUtils.downloadSVG;
import static com.koerriva.bugbrain.engine.gui.nanovg.NanoVGUtils.premultiplyAlpha;
import static java.lang.Math.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.Blendish.*;
import static org.lwjgl.nanovg.NanoSVG.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL2.*;
import static org.lwjgl.nanovg.OUI.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.Checks.CHECKS;
import static org.lwjgl.system.Checks.checkNT1Safe;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Blendish demo.
 *
 * <p>This is a Java port of
 * <a href="https://bitbucket.org/duangle/oui-blendish/src/eb226e17ec5b5bdb323a4a1182a8f7697c8655d3/example.cpp">example.cpp</a>.</p>
 */
public final class BlendishDemo {

    private static final ByteBuffer font;

    static {
        try {
            font = ioResourceToByteBuffer("data/font/NotoSansSC-Regular.otf", 9 * 1024 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BlendishDemo() {
    }

    public static void main(String[] args) {
        GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new RuntimeException("Failed to init GLFW.");
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        long window = glfwCreateWindow(650, 650, "OUI Blendish Demo", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException();
        }

        glfwSetMouseButtonCallback(window, (handle, button, action, mods) -> {
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
        glfwSetCursorPosCallback(window, (handle, xpos, ypos) -> uiSetCursor((int)xpos, (int)ypos));
        glfwSetScrollCallback(window, (handle, xoffset, yoffset) -> uiSetScroll((int)xoffset, (int)yoffset));
        glfwSetCharCallback(window, (handle, codepoint) -> uiSetChar(codepoint));
        glfwSetKeyCallback(window, (handle, keyCode, scancode, action, mods) -> {
            if (keyCode == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(handle, true);
            }
            uiSetKey(keyCode, mods, action != GLFW_RELEASE);
        });

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(1);

        long vg = nvgCreate(NVG_ANTIALIAS);
        if (vg == NULL) {
            throw new RuntimeException("Could not init nanovg.");
        }
        initBlendish(vg);

        OUIState ouiState = new OUIState();

        glfwSetTime(0);

        double c     = 0.0;
        int    total = 0;

        glClearColor(0.3f, 0.3f, 0.32f, 1.0f);
        glfwShowWindow(window);
        while (!glfwWindowShouldClose(window)) {
            int ww, wh;
            int fw, fh;
            try (MemoryStack stack = stackPush()) {
                IntBuffer pw = stack.mallocInt(1);
                IntBuffer ph = stack.mallocInt(1);

                glfwGetWindowSize(window, pw, ph);
                ww = pw.get(0);
                wh = ph.get(0);

                glfwGetFramebufferSize(window, pw, ph);
                fw = pw.get(0);
                fh = ph.get(0);
            }

            // Update and render
            glViewport(0, 0, fw, fh);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            double t = glfwGetTime();

            nvgBeginFrame(vg, ww, wh, fw / (float)ww);

            ouiState.draw(vg, ww, wh);

            nvgEndFrame(vg);
            double t2 = glfwGetTime();
            c += (t2 - t);
            total++;
            if (c >= 1.0) {
                System.out.printf("%.2f ms/frame (~%d fps)\n", (c / total) * 1000.0, round(total / c));
                total = 0;
                c = 0.0;
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        ouiState.destroy();

        nvgDelete(vg);

        GL.setCapabilities(null);

        glfwFreeCallbacks(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private static void initBlendish(long vg) {
        bndSetFont(nvgCreateFontMem(vg, "system", font, 0));

        System.out.println("Downloading Blender icons...");
        long t = System.nanoTime();

        ByteBuffer iconsSVG = downloadSVG("https://raw.githubusercontent.com/sobotka/blender/master/release/datafiles/blender_icons.svg");

        System.out.format("\t%dms\n", (System.nanoTime() - t) / 1000 / 1000);

        NSVGImage svg;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            svg = nsvgParse(iconsSVG, stack.ASCII("px"), 96.0f);
        }
        if (svg == null) {
            throw new IllegalStateException("Failed to parse SVG.");
        }

        long rast = nsvgCreateRasterizer();
        if (rast == NULL) {
            throw new IllegalStateException("Failed to create SVG rasterizer.");
        }

        int w = (int)svg.width();
        int h = (int)svg.height();

        ByteBuffer image = memAlloc(w * h * 4);
        nsvgRasterize(rast, svg, 0, 0, 1, image, w, h, w * 4);
        premultiplyAlpha(image, w, h, w * 4);
        bndSetIconImage(nvgCreateImageRGBA(vg, w, h, NVG_IMAGE_PREMULTIPLIED, image));
    }

}

enum SubType {
    // label
    ST_LABEL,
    // button
    ST_BUTTON,
    // radio button
    ST_RADIO,
    // progress slider
    ST_SLIDER,
    // column
    ST_COLUMN,
    // row
    ST_ROW,
    // check button
    ST_CHECK,
    // panel
    ST_PANEL,
    // text
    ST_TEXT,
    //
    ST_IGNORE,

    ST_DEMOSTUFF,
    // colored rectangle
    ST_RECT,

    ST_HBOX,
    ST_VBOX,
}

// The classes below were generated using the LWJGL generator, then cleaned-up.

class UIData extends Struct implements NativeResource {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int
            SUBTYPE,
            HANDLER;

    static {
        Layout layout = __struct(
                __member(4),
                __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        SUBTYPE = layout.offsetof(0);
        HANDLER = layout.offsetof(1);
    }

    UIData(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public int subtype() { return nsubtype(address()); }
    @Nullable
    @NativeType("UIhandler")
    public UIHandler handler() { return nhandler(address()); }

    public UIData subtype(int value) {
        nsubtype(address(), value);
        return this;
    }
    public UIData handler(@Nullable @NativeType("UIhandler") UIHandlerI value) {
        nhandler(address(), value);
        return this;
    }

    public static UIData create(long address) {
        return wrap(UIData.class, address);
    }

    @Nullable
    public static UIData createSafe(long address) {
        return address == NULL ? null : wrap(UIData.class, address);
    }

    public static int nsubtype(long struct)                              { return UNSAFE.getInt(null, struct + UIData.SUBTYPE); }
    @Nullable public static UIHandler nhandler(long struct)              { return UIHandler.createSafe(memGetAddress(struct + UIData.HANDLER)); }

    public static void nsubtype(long struct, int value)                  { UNSAFE.putInt(null, struct + UIData.SUBTYPE, value); }
    public static void nhandler(long struct, @Nullable UIHandlerI value) { memPutAddress(struct + UIData.HANDLER, memAddressSafe(value)); }

}

class UIRectData extends Struct implements NativeResource {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int
            HEAD,
            LABEL,
            COLOR;

    static {
        Layout layout = __struct(
                __member(UIData.SIZEOF, UIData.ALIGNOF),
                __member(POINTER_SIZE),
                __member(NVGColor.SIZEOF, NVGColor.ALIGNOF)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        HEAD = layout.offsetof(0);
        LABEL = layout.offsetof(1);
        COLOR = layout.offsetof(2);
    }

    UIRectData(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public UIData head() { return nhead(address()); }
    public UIRectData head(java.util.function.Consumer<UIData> consumer) {
        consumer.accept(head());
        return this;
    }
    @Nullable
    @NativeType("char const *")
    public ByteBuffer label() { return nlabel(address()); }
    @Nullable
    @NativeType("char const *")
    public String labelString() { return nlabelString(address()); }
    @NativeType("NVGcolor")
    public NVGColor color() { return ncolor(address()); }
    public UIRectData color(java.util.function.Consumer<NVGColor> consumer) {
        consumer.accept(color());
        return this;
    }

    public UIRectData head(UIData value) {
        nhead(address(), value);
        return this;
    }
    public UIRectData label(@Nullable @NativeType("char const *") ByteBuffer value) {
        nlabel(address(), value);
        return this;
    }
    public UIRectData color(@NativeType("NVGcolor") NVGColor value) {
        ncolor(address(), value);
        return this;
    }

    public static UIRectData create(long address) {
        return wrap(UIRectData.class, address);
    }

    public static UIData nhead(long struct)                  { return UIData.create(struct + UIRectData.HEAD); }
    @Nullable public static ByteBuffer nlabel(long struct)   { return memByteBufferNT1Safe(memGetAddress(struct + UIRectData.LABEL)); }
    @Nullable public static String nlabelString(long struct) { return memUTF8Safe(memGetAddress(struct + UIRectData.LABEL)); }
    public static NVGColor ncolor(long struct)               { return NVGColor.create(struct + UIRectData.COLOR); }

    public static void nhead(long struct, UIData value)      { memCopy(value.address(), struct + UIRectData.HEAD, UIData.SIZEOF); }
    public static void nlabel(long struct, @Nullable ByteBuffer value) {
        if (CHECKS) {
            checkNT1Safe(value);
        }
        memPutAddress(struct + UIRectData.LABEL, memAddressSafe(value));
    }
    public static void ncolor(long struct, NVGColor value) { memCopy(value.address(), struct + UIRectData.COLOR, NVGColor.SIZEOF); }

}

class UIButtonData extends Struct implements NativeResource {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int
            HEAD,
            ICONID,
            LABEL;

    static {
        Layout layout = __struct(
                __member(UIData.SIZEOF, UIData.ALIGNOF),
                __member(4),
                __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        HEAD = layout.offsetof(0);
        ICONID = layout.offsetof(1);
        LABEL = layout.offsetof(2);
    }

    UIButtonData(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public UIData head() { return nhead(address()); }
    public UIButtonData head(java.util.function.Consumer<UIData> consumer) {
        consumer.accept(head());
        return this;
    }
    public int iconid() { return niconid(address()); }
    @Nullable
    @NativeType("char const *")
    public ByteBuffer label() { return nlabel(address()); }
    @Nullable
    @NativeType("char const *")
    public String labelString() { return nlabelString(address()); }

    public UIButtonData head(UIData value) {
        nhead(address(), value);
        return this;
    }
    public UIButtonData iconid(int value) {
        niconid(address(), value);
        return this;
    }
    public UIButtonData label(@Nullable @NativeType("char const *") ByteBuffer value) {
        nlabel(address(), value);
        return this;
    }

    public static UIButtonData create(long address) {
        return wrap(UIButtonData.class, address);
    }

    public static UIData nhead(long struct)                  { return UIData.create(struct + UIButtonData.HEAD); }
    public static int niconid(long struct)                   { return UNSAFE.getInt(null, struct + UIButtonData.ICONID); }
    @Nullable public static ByteBuffer nlabel(long struct)   { return memByteBufferNT1Safe(memGetAddress(struct + UIButtonData.LABEL)); }
    @Nullable public static String nlabelString(long struct) { return memUTF8Safe(memGetAddress(struct + UIButtonData.LABEL)); }

    public static void nhead(long struct, UIData value)      { memCopy(value.address(), struct + UIButtonData.HEAD, UIData.SIZEOF); }
    public static void niconid(long struct, int value)       { UNSAFE.putInt(null, struct + UIButtonData.ICONID, value); }
    public static void nlabel(long struct, @Nullable ByteBuffer value) {
        if (CHECKS) {
            checkNT1Safe(value);
        }
        memPutAddress(struct + UIButtonData.LABEL, memAddressSafe(value));
    }

}

class UICheckData extends Struct implements NativeResource {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int
            HEAD,
            LABEL,
            OPTION;

    static {
        Layout layout = __struct(
                __member(UIData.SIZEOF, UIData.ALIGNOF),
                __member(POINTER_SIZE),
                __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        HEAD = layout.offsetof(0);
        LABEL = layout.offsetof(1);
        OPTION = layout.offsetof(2);
    }

    UICheckData(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public UIData head() { return nhead(address()); }
    public UICheckData head(java.util.function.Consumer<UIData> consumer) {
        consumer.accept(head());
        return this;
    }
    @Nullable
    @NativeType("char const *")
    public ByteBuffer label() { return nlabel(address()); }
    @Nullable
    @NativeType("char const *")
    public String labelString() { return nlabelString(address()); }
    @NativeType("int *")
    public IntBuffer option(int capacity) { return noption(address(), capacity); }

    public UICheckData head(UIData value) {
        nhead(address(), value);
        return this;
    }
    public UICheckData label(@Nullable @NativeType("char const *") ByteBuffer value) {
        nlabel(address(), value);
        return this;
    }
    public UICheckData option(@NativeType("int *") IntBuffer value) {
        noption(address(), value);
        return this;
    }

    public static UICheckData create(long address) {
        return wrap(UICheckData.class, address);
    }

    public static UIData nhead(long struct)                    { return UIData.create(struct + UICheckData.HEAD); }
    @Nullable public static ByteBuffer nlabel(long struct)     { return memByteBufferNT1Safe(memGetAddress(struct + UICheckData.LABEL)); }
    @Nullable public static String nlabelString(long struct)   { return memUTF8Safe(memGetAddress(struct + UICheckData.LABEL)); }
    public static IntBuffer noption(long struct, int capacity) { return memIntBuffer(memGetAddress(struct + UICheckData.OPTION), capacity); }

    public static void nhead(long struct, UIData value)        { memCopy(value.address(), struct + UICheckData.HEAD, UIData.SIZEOF); }
    public static void nlabel(long struct, @Nullable ByteBuffer value) {
        if (CHECKS) {
            checkNT1Safe(value);
        }
        memPutAddress(struct + UICheckData.LABEL, memAddressSafe(value));
    }
    public static void noption(long struct, IntBuffer value) { memPutAddress(struct + UICheckData.OPTION, memAddress(value)); }

}

class UIRadioData extends Struct implements NativeResource {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int
            HEAD,
            ICONID,
            LABEL,
            VALUE;

    static {
        Layout layout = __struct(
                __member(UIData.SIZEOF, UIData.ALIGNOF),
                __member(4),
                __member(POINTER_SIZE),
                __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        HEAD = layout.offsetof(0);
        ICONID = layout.offsetof(1);
        LABEL = layout.offsetof(2);
        VALUE = layout.offsetof(3);
    }

    UIRadioData(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public UIData head() { return nhead(address()); }
    public UIRadioData head(java.util.function.Consumer<UIData> consumer) {
        consumer.accept(head());
        return this;
    }
    public int iconid() { return niconid(address()); }
    @Nullable
    @NativeType("char const *")
    public ByteBuffer label() { return nlabel(address()); }
    @Nullable
    @NativeType("char const *")
    public String labelString() { return nlabelString(address()); }
    @NativeType("int *")
    public IntBuffer value(int capacity) { return nvalue(address(), capacity); }

    public UIRadioData head(UIData value) {
        nhead(address(), value);
        return this;
    }
    public UIRadioData iconid(int value) {
        niconid(address(), value);
        return this;
    }
    public UIRadioData label(@Nullable @NativeType("char const *") ByteBuffer value) {
        nlabel(address(), value);
        return this;
    }
    public UIRadioData value(@NativeType("int *") IntBuffer value) {
        nvalue(address(), value);
        return this;
    }

    public static UIRadioData create(long address) {
        return wrap(UIRadioData.class, address);
    }

    public static UIData nhead(long struct)                   { return UIData.create(struct + UIRadioData.HEAD); }
    public static int niconid(long struct)                    { return UNSAFE.getInt(null, struct + UIRadioData.ICONID); }
    @Nullable public static ByteBuffer nlabel(long struct)    { return memByteBufferNT1Safe(memGetAddress(struct + UIRadioData.LABEL)); }
    @Nullable public static String nlabelString(long struct)  { return memUTF8Safe(memGetAddress(struct + UIRadioData.LABEL)); }
    public static IntBuffer nvalue(long struct, int capacity) { return memIntBuffer(memGetAddress(struct + UIRadioData.VALUE), capacity); }

    public static void nhead(long struct, UIData value)       { memCopy(value.address(), struct + UIRadioData.HEAD, UIData.SIZEOF); }
    public static void niconid(long struct, int value)        { UNSAFE.putInt(null, struct + UIRadioData.ICONID, value); }
    public static void nlabel(long struct, @Nullable ByteBuffer value) {
        if (CHECKS) {
            checkNT1Safe(value);
        }
        memPutAddress(struct + UIRadioData.LABEL, memAddressSafe(value));
    }
    public static void nvalue(long struct, IntBuffer value) { memPutAddress(struct + UIRadioData.VALUE, memAddress(value)); }

}

class UISliderData extends Struct implements NativeResource {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int
            HEAD,
            LABEL,
            PROGRESS;

    static {
        Layout layout = __struct(
                __member(UIData.SIZEOF, UIData.ALIGNOF),
                __member(POINTER_SIZE),
                __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        HEAD = layout.offsetof(0);
        LABEL = layout.offsetof(1);
        PROGRESS = layout.offsetof(2);
    }

    UISliderData(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public UIData head() { return nhead(address()); }
    public UISliderData head(java.util.function.Consumer<UIData> consumer) {
        consumer.accept(head());
        return this;
    }
    @Nullable
    @NativeType("char const *")
    public ByteBuffer label() { return nlabel(address()); }
    @Nullable
    @NativeType("char const *")
    public String labelString() { return nlabelString(address()); }
    @NativeType("float *")
    public FloatBuffer progress(int capacity) { return nprogress(address(), capacity); }

    public UISliderData head(UIData value) {
        nhead(address(), value);
        return this;
    }
    public UISliderData label(@Nullable @NativeType("char const *") ByteBuffer value) {
        nlabel(address(), value);
        return this;
    }
    public UISliderData progress(@NativeType("float *") FloatBuffer value) {
        nprogress(address(), value);
        return this;
    }

    public static UISliderData create(long address) {
        return wrap(UISliderData.class, address);
    }

    public static UIData nhead(long struct)                        { return UIData.create(struct + UISliderData.HEAD); }
    @Nullable public static ByteBuffer nlabel(long struct)         { return memByteBufferNT1Safe(memGetAddress(struct + UISliderData.LABEL)); }
    @Nullable public static String nlabelString(long struct)       { return memUTF8Safe(memGetAddress(struct + UISliderData.LABEL)); }
    public static FloatBuffer nprogress(long struct, int capacity) { return memFloatBuffer(memGetAddress(struct + UISliderData.PROGRESS), capacity); }

    public static void nhead(long struct, UIData value)            { memCopy(value.address(), struct + UISliderData.HEAD, UIData.SIZEOF); }
    public static void nlabel(long struct, @Nullable ByteBuffer value) {
        if (CHECKS) {
            checkNT1Safe(value);
        }
        memPutAddress(struct + UISliderData.LABEL, memAddressSafe(value));
    }
    public static void nprogress(long struct, FloatBuffer value) { memPutAddress(struct + UISliderData.PROGRESS, memAddress(value)); }

}

class UITextData extends Struct implements NativeResource {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int
            HEAD,
            TEXT,
            MAXSIZE;

    static {
        Layout layout = __struct(
                __member(UIData.SIZEOF, UIData.ALIGNOF),
                __member(POINTER_SIZE),
                __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        HEAD = layout.offsetof(0);
        TEXT = layout.offsetof(1);
        MAXSIZE = layout.offsetof(2);
    }

    UITextData(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    public UIData head() { return nhead(address()); }
    public UITextData head(java.util.function.Consumer<UIData> consumer) {
        consumer.accept(head());
        return this;
    }
    @Nullable
    @NativeType("char *")
    public ByteBuffer text() { return ntext(address()); }
    @Nullable
    @NativeType("char *")
    public String textString() { return ntextString(address()); }
    public int maxsize() { return nmaxsize(address()); }

    public UITextData head(UIData value) {
        nhead(address(), value);
        return this;
    }
    public UITextData text(@Nullable @NativeType("char *") ByteBuffer value) {
        ntext(address(), value);
        return this;
    }
    public UITextData maxsize(int value) {
        nmaxsize(address(), value);
        return this;
    }

    public static UITextData create(long address) {
        return wrap(UITextData.class, address);
    }

    public static UIData nhead(long struct)                 { return UIData.create(struct + UITextData.HEAD); }
    @Nullable public static ByteBuffer ntext(long struct)   { return memByteBufferNT1Safe(memGetAddress(struct + UITextData.TEXT)); }
    @Nullable public static String ntextString(long struct) { return memUTF8Safe(memGetAddress(struct + UITextData.TEXT)); }
    public static int nmaxsize(long struct)                 { return UNSAFE.getInt(null, struct + UITextData.MAXSIZE); }

    public static void nhead(long struct, UIData value)     { memCopy(value.address(), struct + UITextData.HEAD, UIData.SIZEOF); }
    public static void ntext(long struct, @Nullable ByteBuffer value) {
        if (CHECKS) {
            checkNT1Safe(value);
        }
        memPutAddress(struct + UITextData.TEXT, memAddressSafe(value));
    }
    public static void nmaxsize(long struct, int value) { UNSAFE.putInt(null, struct + UITextData.MAXSIZE, value); }

}
