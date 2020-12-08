package ui;

import editor.Editor;
import editor.Toolbar;
import editor.ViewTool;
import processing.core.PApplet;

public class ViewModeWidget extends Widget {
	public ViewModeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		//icon = p.loadImage(folder + "colider.png");
		imageInactive = null;
		active = true;
	}

	public void clicked() {
		editor.currentTool = new ViewTool(p, editor);
	}

	public void updateActive() {
		if (editor.currentTool instanceof ViewTool) {
			active = true;
		} else {
			active = false;
		}
	}
}
