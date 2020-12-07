package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class SnapWidget extends Widget {
	public SnapWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "snaptoGrid.png");
	}

	public void clicked() {
		editor.snap = !editor.snap;
	}

	public void updateActive() {
		if (editor.snap) {
			active = true;
		} else {
			active = false;
		}
	}
}
