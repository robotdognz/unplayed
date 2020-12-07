package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class QuadtreeWidget extends Widget {
	public QuadtreeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "quadTree.png");
	}

	public void clicked() {
		editor.quadtree = !editor.quadtree;
	}

	public void updateActive() {
		if (editor.quadtree) {
			active = true;
		} else {
			active = false;
		}
	}
}
