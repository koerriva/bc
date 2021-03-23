package com.koerriva.project002;

import com.koerriva.project002.core.game.game.Game;

import java.io.IOException;

public class BrainEditor {
    public static void main(String[] args) throws IOException {
        Game game = new Game(800,600,"BrainBoard");
        game.init();
        game.run();
    }
}
