package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Editable;
import processing.core.PApplet;
import ui.Widget;

public class WidgetFlipH extends Widget {
	EditorSide toolbar;

	public WidgetFlipH(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "FlipHoz.png");
	}

	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Editable) {
			available = true;
			if (toolbar.isFlippedH()) {
				active = true;
			} else {
				active = false;
			}
		} else {
			available = false;
		}

	}

	public void clicked() {
		toolbar.flipH();
	}

}
