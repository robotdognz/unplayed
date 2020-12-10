package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetDelete extends Widget {
	public WidgetDelete(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "confirmTick.png");
		closeAfterSubWidget = true;
	}

	public void clicked() {
		
	}

	public void updateActiveUser() {
		if (editor.selected != null) {
			available = true;
		} else {
			available = false;
		}
	}

}
