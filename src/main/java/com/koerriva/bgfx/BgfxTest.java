package com.koerriva.bgfx;

import org.lwjgl.bgfx.BGFX;
import org.lwjgl.bgfx.BGFXInit;
import org.lwjgl.glfw.GLFWNativeWin32;

import static org.lwjgl.bgfx.BGFX.*;
import static org.lwjgl.glfw.GLFW.*;

public class BgfxTest {
    public static void main(String[] args) {
        int width=1280,height=720;

        glfwInit();
        glfwWindowHint(GLFW_CLIENT_API,GLFW_NO_API);
        long window = glfwCreateWindow(width,height,"BGFX",0,0);

        BGFXInit init = BGFXInit.malloc();
        BGFX.bgfx_init_ctor(init);
        init.resolution(it->it.width(width).height(height).reset(BGFX.BGFX_RESET_VSYNC));
        init.platformData().nwh(GLFWNativeWin32.glfwGetWin32Window(window));
        init.vendorId(BGFX_PCI_ID_NVIDIA);

        boolean success = BGFX.bgfx_init(init);
        if(!success)throw new RuntimeException("BGFX 初始化失败");
        System.out.println("renderer: " + bgfx_get_renderer_name(bgfx_get_renderer_type()));

        bgfx_set_debug(BGFX_DEBUG_TEXT);
        BGFX.bgfx_set_view_clear(0,BGFX.BGFX_CLEAR_COLOR,0x443355FF, 1.0f, 0);

        while (!glfwWindowShouldClose(window)){
            glfwPollEvents();
            BGFX.bgfx_set_view_rect(0,0,0,width,height);
            bgfx_touch(0);

            bgfx_dbg_text_clear(0, false);
            bgfx_dbg_text_printf(0, 1, 0x1f, "bgfx/examples/25-c99中文");
            bgfx_frame(false);
        }

        bgfx_shutdown();

        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
