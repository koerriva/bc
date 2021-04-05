package com.koerriva.bugbrain.engine.gui.nuklear;

import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/glfw_opengl3/main.c">https://github.com/vurtun/nuklear/blob/master/demo/glfw_opengl3/main.c</a>.
 */
public class Editor {

    private static final int EASY = 0;
    private static final int HARD = 1;

    NkColorf background = NkColorf.create()
            .r(0.10f)
            .g(0.18f)
            .b(0.24f)
            .a(1.0f);

    private int op = EASY;

    private IntBuffer compression = BufferUtils.createIntBuffer(1).put(0, 20);

    public Editor() {
    }

    public void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            if (nk_begin(
                    ctx,
                    "",
                    nk_rect(x, y, 250, 42, rect),
                    NK_WINDOW_NO_SCROLLBAR|NK_WINDOW_BACKGROUND|NK_WINDOW_BORDER
            )) {
                nk_layout_row_dynamic(ctx, 30, 3);
                if (nk_button_label(ctx, "神经元+")) {
                    System.out.println("button pressed");
                }
                if (nk_button_label(ctx, "神经元+")) {
                    System.out.println("button pressed");
                }
                if (nk_button_label(ctx, "神经元+")) {
                    System.out.println("button pressed");
                }
                nk_layout_row_end(ctx);
            }
            nk_end(ctx);
        }
    }

}
