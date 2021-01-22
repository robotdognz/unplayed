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

	@Override
	public void clicked() {
		editor.tileSearch = !editor.tileSearch;
//		editor.quadVis = !editor.quadVis;
	}

	@Override
	public void updateActive() {
		super.updateActive();
//		if (editor.quadVis) {
//			active = true;
//		} else {
//			active = false;
//		}
		if (editor.tileSearch) {
			active = true;
		} else {
			active = false;
		}
	}
}
