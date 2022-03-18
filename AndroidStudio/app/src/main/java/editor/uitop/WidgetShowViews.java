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

	@Override
	public void clicked() {
		editor.viewVis = !editor.viewVis;
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.viewVis) {
			active = true;
		} else {
			active = false;
		}
	}
}
