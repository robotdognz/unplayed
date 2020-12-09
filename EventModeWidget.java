package editor.bottom;

import editor.Editor;
import editor.EventTool;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class EventModeWidget extends Widget {
	public EventModeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "event.png");
		imageInactive = null;
	}

	public void clicked() {
		editor.currentTool = new EventTool(editor);
	}

	public void updateActive() {
		if (editor.currentTool instanceof EventTool) {
			active = true;
		} else {
			active = false;
		}
	}
}