package controllers;

import java.util.ArrayList;

import editor.Editor;
import game.Game;
import misc.Converter;
import processing.core.PApplet;
import processing.core.PVector;

public class CameraControl implements Controller {
	private PApplet p;
	private Game game;
	private Editor editor;
	private Converter convert;
	int maxZoomSpeed;

	public CameraControl(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
		this.game = editor.game;
		this.convert = game.convert;
		maxZoomSpeed = 150;
	}

	public void step(ArrayList<PVector> touch) {
		game.player.still();
	}

	public void touchStarted(PVector touch) {
	}

	public void touchEnded(PVector touch) {
	}

	public void touchMoved(ArrayList<PVector> touch) {
		if (touch.size() == 1) {
			float moveX = (p.pmouseX - p.mouseX) / 3;
			float moveY = (p.pmouseY - p.mouseY) / 3;
			PVector diff = new PVector(convert.screenToLevel(moveX), convert.screenToLevel(moveY));
			editor.camera.setCenter(editor.camera.getCenter().add(diff));
		}
	}

	public void onPinch(ArrayList<PVector> touch, float x, float y, float d) {
		// TODO make this zoom from the center of the gesture, not the center of the
		// screen
		if (d > maxZoomSpeed) {
			d = maxZoomSpeed;
		}
		if (d < -maxZoomSpeed) {
			d = -maxZoomSpeed;
		}

		if (touch.size() == 2) {
			float newScale = editor.camera.getScale() - convert.screenToLevel(d);
			float newTotalScale = convert.getTotalFromScale(newScale);
			if (newTotalScale < editor.minZoom) {
				newScale = convert.getScaleFromTotal(editor.minZoom);
			}
			if (newTotalScale > editor.maxZoom) {
				newScale = convert.getScaleFromTotal(editor.maxZoom);
			}
			editor.camera.setScale(newScale);
		}
	}
	
	public void onRotate(float x, float y, float angle) {
	}
}
