package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSnap extends Widget {
	public WidgetSnap(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "snaptoGrid.png");
	}

	@Override
	public void clicked() {
		editor.snap = !editor.snap;
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.snap) {
			active = true;
		} else {
			active = false;
		}
	}
}
