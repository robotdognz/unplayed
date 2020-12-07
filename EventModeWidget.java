package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class EventModeWidget extends Widget {
	public EventModeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "event.png");
		imageInactive = null;
	}

	public void clicked() {
		editor.eType = Editor.editorType.EVENT;
	}

	public void updateActive() {
		if (editor.eType == Editor.editorType.EVENT) {
			active = true;
		} else {
			active = false;
		}
	}
}