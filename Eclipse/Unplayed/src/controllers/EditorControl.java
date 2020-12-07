package controllers;

import java.util.ArrayList;

import editor.Editor;
import game.Game;
import misc.Converter;
import processing.core.PApplet;
import processing.core.PVector;

public class EditorControl implements Controller {
	private PApplet p;
	private Editor editor;
	private Game game;
	private Converter convert;

	public EditorControl(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
		this.game = editor.game;
		this.convert = game.convert;
	}

	public void step(ArrayList<PVector> touch) {
		game.player.still();
	}

	public void touchStarted(PVector touch) {
	}

	public void touchEnded() {
	}

	public void touchMoved(ArrayList<PVector> touch) {
		float snapNo = 10;
		if (editor.snap) {
			snapNo = 100;
		}

		// calculate position in level
		PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
		//// round so blocks snap to grid
		float finalX = Math.round((placement.x - 50) / snapNo) * snapNo;
		float finalY = Math.round((placement.y - 50) / snapNo) * snapNo;

		game.point = new PVector(finalX, finalY);
		if (editor.snap) {
			editor.editWorld();
		}
	}

	public void onPinch(ArrayList<PVector> touch, float x, float y, float d) {
	}
}
