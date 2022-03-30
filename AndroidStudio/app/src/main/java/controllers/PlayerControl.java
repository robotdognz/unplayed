package controllers;

import java.util.ArrayList;

import game.AppLogic;
import game.Game;
import processing.core.*;

public class PlayerControl implements Controller {
    private final PApplet p;
    private final Game game;

    public PlayerControl(PApplet p, Game game) {
        this.p = p;
        this.game = game;
    }

    @Override
    public void step(ArrayList<PVector> touch) {
        // prevent input when game paused
        if (game.isPaused()) {
            return;
        }

        if (game.player != null) {
            int left = 0;
            int right = 0;
            for (PVector t : touch) {
                if (t.y >= p.height / 3f) {
                    if (t.x < p.width / 3f) { // p.width * 0.25f // left chunk of screen
                        left++;
                    }
                    if (t.x > p.width - p.width / 3f) { // p.width * 0.75f // right chunk of screen
                        right++;
                    }
                }
            }
            if (left > right) {
                game.player.left();
                AppLogic.drawControls.left();
            } else if (left < right) {
                game.player.right();
                AppLogic.drawControls.right();
            } else {
                game.player.still();
            }

        }
    }

    @Override
    public void touchStarted(PVector touch) {
        // prevent input when game paused
        if (game.isPaused()) {
            return;
        }

        if (game.player != null) {
            // jump if the last true touch was in the middle of the screen
            if (touch.y >= p.height / 3f && touch.x > p.width / 4f && touch.x < (p.width / 4f) * 3) {
                game.player.jump();
//                AppLogic.drawControls.jump();
            }
        }
    }

    @Override
    public void touchEnded(PVector touch) {
    }

    @Override
    public void touchMoved(PVector touch, ArrayList<PVector> touches) {
    }

    @Override
    public void onPinch(ArrayList<PVector> touch, float x, float y, float d) {
    }

    @Override
    public void onRotate(float x, float y, float angle) {
    }
}
