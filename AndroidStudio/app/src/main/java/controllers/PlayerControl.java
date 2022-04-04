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

        // prevent input if no player
        if (game.player == null) {
            return;
        }

        int left = 0;
        int right = 0;

        for (PVector t : touch) {
            // continue if touch below controls
            if (t.y > AppLogic.drawUI.getControlsBottom()) {
                continue;
            }

            // continue if touch above controls
            if (t.y < AppLogic.drawUI.getControlsTop()) { // t.y < p.height / 3f
                continue;
            }

            // do left and right control regions
            if (t.x < p.width / 3f) { // left chunk of screen
                left++;
            }
            if (t.x > p.width - p.width / 3f) { // right chunk of screen
                right++;
            }
        }
        if (left > right) {
            game.player.left();
            AppLogic.drawUI.left();
        } else if (left < right) {
            game.player.right();
            AppLogic.drawUI.right();
        } else {
            game.player.still();
        }

    }

    @Override
    public void touchStarted(PVector touch) {
        // prevent input when game paused
        if (game.isPaused()) {
            return;
        }

        if (game.player != null) {

            // return if touch below controls
            if (touch.y > AppLogic.drawUI.getControlsBottom()) {
                return;
            }

            // return if touch above controls
            if (touch.y < AppLogic.drawUI.getControlsTop()) {
                return;
            }

            // jump if the touch is in the middle third of the screen
            if (touch.x > p.width / 4f && touch.x < (p.width / 4f) * 3) {
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

    @Override
    public void onTap(float x, float y){
    }
}
