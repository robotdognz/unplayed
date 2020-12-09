package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetQuadtree extends Widget {
	public WidgetQuadtree(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "quadTree.png");
	}

	public void clicked() {
		editor.quadVis = !editor.quadVis;
	}

	public void updateActive() {
		if (editor.quadVis) {
			active = true;
		} else {
			active = false;
		}
	}
}
