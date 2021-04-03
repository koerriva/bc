package com.koerriva.bugbrain.core.gui;

import com.koerriva.bugbrain.core.game.Window;

public interface GUI {
    void init(Window window);
    void update(Window window);
    void render(Window window);
    void cleanup();
}
