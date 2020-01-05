package com.codingame.game;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    static final Pattern PLAYER_PATTERN = Pattern.compile(
            "^(?<x>\\d+)\\s+(?<y>\\d+)\\s+(?<dir>[UDRL])\\s+(?<action>[\\+\\-])",
            Pattern.CASE_INSENSITIVE);

    @Override
    public void init() {
        graphicEntityModule.createRectangle().setWidth(1920).setHeight(1080).setFillColor(0x333333);
        gameManager.setFrameDuration(1);
        NumberShifting.setSeed(Long.parseLong(gameManager.getTestCaseInput().get(0)));
        NumberShifting.createPasswords();
    }

    NumberShifting shifting;

    @Override
    public void gameTurn(int turn) {
        Player player = gameManager.getPlayer();
        if (turn == 2) {
            gameManager.setFrameDuration(1000);
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
            } else {
                Matcher match = PLAYER_PATTERN.matcher(outputs.get(0));
                if (match.matches()) {
                    int x = Integer.parseInt(match.group("x"));
                    int y = Integer.parseInt(match.group("y"));
                    String dir = match.group("dir").toUpperCase();
                    String action = match.group("action").toUpperCase();
                    shifting.apply(x, y, dir, action);
                } else throw new Exception("invalid command: " + outputs.get(0));
            }
        } catch (TimeoutException e) {
            gameManager.loseGame("timeout");
            return;
        } catch (Exception e) {
            if (turn == 1) gameManager.loseGame("Invalid level code");
            else gameManager.loseGame(e.getMessage());
            return;
        }

        if (shifting.solved()) {
            gameManager.putMetadata("Level", String.valueOf(shifting.getLevel() + 1));
            gameManager.addToGameSummary("Code for next level (level " + (shifting.getLevel() + 2) + "): " + shifting.nextLevel());
            for (String line : new NumberShifting(shifting.getLevel() + 1).exportMap())
                gameManager.addToGameSummary(line);
            gameManager.winGame();
        }
    }
}
