package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetRotate extends Widget {
	EditorSide toolbar;

	public WidgetRotate(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "rotateClockwise.png");
	}

	public void updateActiveUser() {
		if (editor.selected != null && editor.selected instanceof Page) {
			available = true;
			if (toolbar.mode == EditorSide.ModifyMode.ROTATE) {
				active = true;
			} else {
				active = false;
			}
		} else {
			available = false;
		}

	}

	public void clicked() {
		toolbar.mode = EditorSide.ModifyMode.ROTATE;
	}

}

