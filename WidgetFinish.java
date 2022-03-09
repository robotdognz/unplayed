package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetFinish extends Widget {
	EditorSide toolbar;

	public WidgetFinish(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "confirmTick.png");
	}

	@Override
	public void clicked() {
		editor.selected = null;
		editor.nextTouchInactive = true; // prevent selecting or erasing after clicking this widget

	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null) {
			available = true;
		} else {
			available = false;
		}
	}
}
