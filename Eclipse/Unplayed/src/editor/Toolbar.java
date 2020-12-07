package editor;

import java.util.ArrayList;

import menus.Menu;
import processing.core.PVector;
import ui.Widget;

public abstract class Toolbar {
	public ArrayList<Widget> eWidgets;
	public float eWidgetSpacing; // size of gap between widgets
	public float eWidgetOffset; // amount to offset widget drawing by

	public Editor editor;

	public Toolbar(Editor editor) {
		eWidgetSpacing = 0;
		eWidgetOffset = 0;
		this.editor = editor;
	}

	public void step() {
	}

	public void draw(PVector touch, Menu menu) {
	}

	public void touchStarted() {
	}

	public void touchEnded() {
	}

	public void touchMoved() {
	}

	public void onTap(float x, float y) {
	}

	public void onPinch(float x, float y, float d) {
	}

	public float getHeight() {
		return 0;
	}
}