package controllers;

import java.util.ArrayList;
import editor.Editor;
import game.AppLogic;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class EditorControl implements Controller {
	private PApplet p;
	private Editor editor;

	public EditorControl(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
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
		if(editor.currentTool != null) {
			editor.currentTool.touchEnded(touch);
		}
	}

	@Override
	public void touchMoved(PVector touch, ArrayList<PVector> touches) {
		//TODO: this logic should potentially be moved directly into the tools
		float snapNo = 10;
		if (editor.snap) {
			snapNo = 100;
		}

		// calculate position in level
		PVector placement = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
		//// round so blocks snap to grid
		float finalX = Math.round((placement.x - 50) / snapNo) * snapNo;
		float finalY = Math.round((placement.y - 50) / snapNo) * snapNo;

		editor.point = new Rectangle(finalX, finalY, 100, 100);
		if (editor.snap && editor.currentTool != null) {
			editor.currentTool.touchMoved(touch);
		}
	}

	@Override
	public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
		if (editor.currentTool != null) {
			editor.currentTool.onPinch(touches, x, y, d);
		}
	}
	
	@Override
	public void onRotate(float x, float y, float angle) {
		if (editor.currentTool != null) {
			editor.currentTool.onRotate(x, y, angle);
		}
	}

	@Override
	public void onTap(float x, float y) {
		if (editor.currentTool != null) {
			editor.currentTool.onTap(x, y);
		}
	}
}
