package com.codingame.game;
import java.util.ArrayList;
import java.util.List;

import NumberShifting.NumberShifting;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject
    private SoloGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    NumberShifting shifting;

    @Override
    public void init() {
        graphicEntityModule.createRectangle().setWidth(1920).setHeight(1080).setFillColor(0x333333);
    }

    @Override
    public void gameTurn(int turn) {
        Player player = gameManager.getPlayer();
        if (turn == 2) {
            for (String s : shifting.exportMap()) {
                player.sendInputLine(s);
            }
        }
        player.execute();

        try {
            List<String> outputs = player.getOutputs();
            if (turn == 1) {
                shifting = new NumberShifting(outputs.get(0));
                shifting.drawBoard(graphicEntityModule);
            } else shifting.apply(outputs.get(0));
        } catch (TimeoutException e) {
            gameManager.loseGame("timeout");
        } catch (Exception e) {
            gameManager.loseGame("invalid command");
        }

        if (shifting.solved()) {
            gameManager.putMetadata("Level", String.valueOf(shifting.getLevel() + 1));
            gameManager.addToGameSummary("Code for next level: " + shifting.nextLevel());
            for (String line : new NumberShifting(shifting.getLevel() + 1).exportMap())
                gameManager.addToGameSummary(line);
            gameManager.winGame();
        }
    }
}
