package controllers;

import java.util.ArrayList;

import game.Game;
import processing.core.*;

public class PlayerControl implements Controller {
	private PApplet p;
	private Game game;

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
				if (t.y >= p.height / 3) {
					if (t.x < p.width * 0.25f) { // / 4) { // left chunk of screen
						left++;
					}
					if (t.x > p.width * 0.75f) { // (p.width / 4) * 3) { // right chunk of screen
						right++;
					}
				}
			}
			if (left > right) {
				game.player.left();
			} else if (left < right) {
				game.player.right();
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
			if (touch.y >= p.height / 3 && touch.x > p.width / 4 && touch.x < (p.width / 4) * 3) {
				game.player.jump();
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
