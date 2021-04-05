package com.koerriva.bugbrain;

import com.koerriva.bugbrain.core.Game;

public class BrainEditor {
    public static void main(String[] args) throws Exception {
        Game game = new Game(800,600,"BrainBoard");
        game.init();
        game.run();
    }
}
