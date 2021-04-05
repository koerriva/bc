package com.koerriva.bugbrain.engine.gui;

import com.koerriva.bugbrain.engine.graphics.Window;

public interface GUI {
    void init(Window window);
    void update(Window window);
    void render(Window window);
    void cleanup();
}
