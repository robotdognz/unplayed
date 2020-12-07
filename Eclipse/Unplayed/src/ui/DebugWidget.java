package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class DebugWidget extends Widget {
	public DebugWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "debugging.png");
	}

	public void clicked() {
		editor.debug = !editor.debug;
	}

	public void updateActive() {
		if (editor.debug) {
			active = true;
		} else {
			active = false;
		}
	}
}