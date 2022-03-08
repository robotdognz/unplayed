package editor;

import java.util.ArrayList;

import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;
import ui.Menu;
import ui.Widget;

public abstract class Toolbar {
	protected PApplet p;
	public ArrayList<Widget> widgets;
	public float widgetSpacing; // size of gap between widgets
	public float widgetOffset; // amount to offset widget drawing by
	protected Rectangle bounds;
	protected String folder;
	public Editor editor;

	public Toolbar(PApplet p, Editor editor) {
		this.p = p;
		widgetSpacing = 0;
		widgetOffset = 0;
		this.editor = editor;
	}

	public boolean insideBoundary(float x, float y) {
		if (bounds != null) {
			if (x > bounds.getBottomRight().x) {
				return false;
			}
			if (x < bounds.getTopLeft().x) {
				return false;
			}
			if (y > bounds.getBottomRight().y) {
				return false;
			}
			if (y < bounds.getTopLeft().y) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public void step() {
	}
	
	public void clearExternalModes() {
	}

	public void draw(PVector touch, Menu menu, float deltaTime) {
		if (bounds != null) {
			p.noStroke();
			p.fill(100, 100, 100, 100);
			p.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		}
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
	
	public void resetWidgets() {
		for(Widget w : widgets) {
			w.reset();
		}
	}
}