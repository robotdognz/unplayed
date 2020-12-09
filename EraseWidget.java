package editor.top;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;
import controllers.EditorControl;

public class EraseWidget extends Widget {
	public EraseWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "eraser.png");
	}

	public void clicked() {
		editor.eMode = Editor.editorMode.ERASE;
		editor.controller = new EditorControl(p, editor);
	}

	public void updateActive() {
		if (editor.eMode == Editor.editorMode.ERASE) {
			active = true;
		} else {
			active = false;
		}
	}
}
