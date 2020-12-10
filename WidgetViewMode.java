package editor.uibottom;

import editor.Editor;
import editor.Toolbar;
import editor.tools.ViewTool;
import processing.core.PApplet;
import ui.Widget;

public class WidgetViewMode extends Widget {
	public WidgetViewMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "View.png");
		imageInactive = null;
		active = false;
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
