package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetDebug extends Widget {
	public WidgetDebug(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "debugging.png");
	}

	public void clicked() {
		editor.debugVis = !editor.debugVis;
	}

	public void updateActive() {
		super.updateActive();
		if (editor.debugVis) {
			active = true;
		} else {
			active = false;
		}
	}
}