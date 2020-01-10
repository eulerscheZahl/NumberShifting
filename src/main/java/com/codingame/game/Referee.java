package com.codingame.game;
import java.util.Arrays;
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
        gameManager.setMaxTurns(600);
        String[] seedText = gameManager.getTestCaseInput().get(0).split(",");
        byte[] seed = new byte[seedText.length];
        for (int i = 0; i < seed.length; i++) {
            seed[i] = Byte.parseByte(seedText[i]);
        }
        NumberShifting.setSeed(seed);
        try {
            NumberShifting.createPasswords();
        } catch (Exception ex) {
            // crypto failed? we will all die
        }
    }

    private NumberShifting shifting;
    private int lastSolve;

    @Override
    public void gameTurn(int turn) {
        int realTurn = turn;
        turn -= lastSolve;
        Player player = gameManager.getPlayer();
        if (realTurn == 2 || turn == 1 && shifting != null) {
            gameManager.setFrameDuration(1000);
            for (String s : shifting.exportMap()) {
                player.sendInputLine(s);
            }
        }
        player.execute();

        try {
            List<String> outputs = player.getOutputs();
            boolean playTurn = true;
            if (turn == 1) {
                if (shifting == null) {
                    shifting = new NumberShifting(outputs.get(0));
                    playTurn = false;
                }
                shifting.drawBoard(graphicEntityModule);
            }
            if (playTurn) {
                gameManager.setTurnMaxTime(50);
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
            if (lastSolve != 0) gameManager.winGame();
            else gameManager.loseGame("timeout");
            return;
        } catch (Exception e) {
            if (lastSolve != 0) gameManager.winGame();
            else if (turn == 1) gameManager.loseGame("Invalid level code");
            else gameManager.loseGame(e.getMessage());
            return;
        }

        if (shifting.solved()) {
            gameManager.putMetadata("Level", String.valueOf(shifting.getLevel() + 1));
            gameManager.addToGameSummary("Code for next level (level " + (shifting.getLevel() + 2) + "): " + shifting.nextLevel());
            try {
                shifting = new NumberShifting(shifting.getLevel() + 1);
            } catch (Exception ex) {
                // worked before, will work now
            }
            for (String line : shifting.exportMap())
                gameManager.addToGameSummary(line);
            gameManager.setTurnMaxTime(800);
            lastSolve = realTurn;
            graphicEntityModule.createRectangle().setWidth(1920).setHeight(1080).setFillColor(0x333333);
        }
    }
}
