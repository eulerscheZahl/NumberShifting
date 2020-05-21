package com.codingame.game;
import com.codingame.gameengine.core.AbstractSoloPlayer;

public class Player extends AbstractSoloPlayer {
    public int lineCount = 1;
    @Override
    public int getExpectedOutputLines() {
        return lineCount;
    }
}
