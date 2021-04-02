package com.koerriva.project002.core.game.gui;

import com.koerriva.project002.core.game.game.Window;

public interface GUI {
    void init(Window window);
    void update(Window window);
    void render(Window window);
    void cleanup();
}
