package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Editable;
import processing.core.PApplet;
import ui.Widget;

public class WidgetFlipV extends Widget {
	EditorSide toolbar;
	
	public WidgetFlipV(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "FlipHVert.png");
	}
	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Editable) {
			available = true;
			if (toolbar.isFlippedV()) {
				active = true;
			} else {
				active = false;
			}
		} else {
			available = false;
		}
	}
	
	@Override
	public void clicked() {
		toolbar.flipV();
	}

}
