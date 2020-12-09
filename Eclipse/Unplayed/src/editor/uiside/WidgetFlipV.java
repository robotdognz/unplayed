package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetFlipV extends Widget {
	EditorSide toolbar;
	
	public WidgetFlipV(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "FlipHVert.png");
	}
	public void updateActiveUser() {
		if (toolbar.isFlippedV()) {
			active = true;
		} else {
			active = false;
		}
	}
	
	public void clicked() {
		toolbar.flipV();
	}

}
