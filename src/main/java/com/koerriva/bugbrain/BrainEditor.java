package com.koerriva.bugbrain;

import com.koerriva.bugbrain.core.game.Game;

import java.io.IOException;

public class BrainEditor {
    public static void main(String[] args) throws IOException {
        Game game = new Game(800,600,"BrainBoard");
        game.init();
        game.run();
    }
}
