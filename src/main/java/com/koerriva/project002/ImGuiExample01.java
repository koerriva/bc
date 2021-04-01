package com.koerriva.project002;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;

public class ImGuiExample01 extends Application {
    public static void main(String[] args) {
        launch(new ImGuiExample01());
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("测试");
    }

    @Override
    public void process() {
        ImGui.text("你好世界");
    }
}
