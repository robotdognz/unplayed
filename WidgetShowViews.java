package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetShowViews extends Widget {
	public WidgetShowViews(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "View.png");
	}

	public void clicked() {
		editor.pageVis = !editor.pageVis;
	}

	public void updateActive() {
		super.updateActive();
		if (editor.pageVis) {
			active = true;
		} else {
			active = false;
		}
	}
}