package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetAdjust extends Widget {
	EditorSide toolbar;

	public WidgetAdjust(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "rotateClockwise.png");
	}

	public void updateActiveUser() {
		if (editor.selected != null && editor.selected instanceof Page) {
			available = true;
			if (toolbar.adjust) {
				active = true;
			} else {
				active = false;
			}
		} else {
			available = false;
		}

	}

	public void clicked() {
		toolbar.adjust = !toolbar.adjust;
	}

}

