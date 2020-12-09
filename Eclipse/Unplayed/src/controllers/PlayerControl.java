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
	
	public void step(ArrayList<PVector> touch) {
		int left = 0;
		int right = 0;
		for (PVector t : touch) {	//TODO: check if this works, it used to directly access the touch screen touches array
			// if (t.y >= height/3) {
			if (t.x < p.width / 4) {
				left++;
			}
			if (t.x > (p.width / 4) * 3) {
				right++;
			}
			// }
		}
		if (left > right) {
			game.player.left();
		} else if (left < right) {
			game.player.right();
		} else {
			game.player.still();
		}
	}

	public void touchStarted(PVector touch) {
		// jump if the last true touch was in the middle of the screen
		if (
		// lastTouch.y >= height/3 &&
		touch.x > p.width / 4 && touch.x < (p.width / 4) * 3) { //TODO: check if this works, it used to directly access lastTouch
			game.player.jump();
		}
	}

	public void touchEnded(PVector touch) {
	}

	public void touchMoved(ArrayList<PVector> touch) {
	}

	public void onPinch(ArrayList<PVector> touch, float x, float y, float d) {
	}
}
