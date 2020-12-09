package editor;

import java.util.ArrayList;

import processing.core.PVector;
import ui.Menu;
import ui.Widget;

public abstract class Toolbar {
	public ArrayList<Widget> widgets;
	public float widgetSpacing; // size of gap between widgets
	public float widgetOffset; // amount to offset widget drawing by

	public Editor editor;

	public Toolbar(Editor editor) {
		widgetSpacing = 0;
		widgetOffset = 0;
		this.editor = editor;
	}

	public boolean insideBoundary(float x, float y) {
		// TODO: need to add a boundary rectangle and complete this method
		// should implement the basic single rectangle version in the abstract class
		// and implement the advanced version inside the sub class where necessary
		return false;
	}

	public void step() {
	}

	public void draw(PVector touch, Menu menu) {
	}

	public void touchStarted() {
	}

	public void touchEnded() {
	}

	public void touchMoved(ArrayList<PVector> touch) {
	}

	public void onTap(float x, float y) {
	}

	public void onPinch(float x, float y, float d) {
	}

	public float getHeight() {
		return 0;
	}
}