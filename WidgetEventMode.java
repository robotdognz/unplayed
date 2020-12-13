package editor.uibottom;

import editor.Editor;
import editor.Toolbar;
import editor.tools.EventTool;
import processing.core.PApplet;
import ui.Widget;

public class WidgetEventMode extends Widget {
	public WidgetEventMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "event.png");
		imageInactive = null;
	}

	public void clicked() {
		editor.currentTool = new EventTool(editor);
	}

	public void updateActive() {
		super.updateActive();
		if (editor.currentTool instanceof EventTool) {
			active = true;
		} else {
			active = false;
		}
	}
}