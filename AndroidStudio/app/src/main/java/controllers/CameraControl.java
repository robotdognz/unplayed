package controllers;

import java.util.ArrayList;

import camera.Camera;
import editor.Editor;
import game.AppLogic;
import processing.core.PApplet;
import processing.core.PVector;

public class CameraControl implements Controller {
	private PApplet p;
	private Editor editor;
	int maxZoomSpeed;

	public CameraControl(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
		maxZoomSpeed = 150;
	}

	@Override
	public void step(ArrayList<PVector> touch) {
		AppLogic.game.stopPlayer();
	}

	@Override
	public void touchStarted(PVector touch) {
	}

	@Override
	public void touchEnded(PVector touch) {
	}

	@Override
	public void touchMoved(PVector touch, ArrayList<PVector> touches) {
		if (touches.size() == 1) {
			float moveX = (p.pmouseX - p.mouseX) / 3;
			float moveY = (p.pmouseY - p.mouseY) / 3;
			PVector diff = new PVector(AppLogic.convert.screenToLevel(moveX), AppLogic.convert.screenToLevel(moveY));
			Camera.setCenter(Camera.getCenter().add(diff));
		}
	}

	@Override
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
			float newScale = Camera.getScale() - AppLogic.convert.screenToLevel(d);
			float newTotalScale = AppLogic.convert.getTotalFromScale(newScale);
			if (newTotalScale < editor.minZoom) {
				newScale = AppLogic.convert.getScaleFromTotal(editor.minZoom);
			}
			if (newTotalScale > editor.maxZoom) {
				newScale = AppLogic.convert.getScaleFromTotal(editor.maxZoom);
			}
			Camera.setScale(newScale);
		}
	}

	@Override
	public void onRotate(float x, float y, float angle) {
	}
}
