package ui;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class SelectWidget extends Widget {
	public SelectWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "select.png");
	}

	public void clicked() {
		editor.eMode = Editor.editorMode.SELECT;
		editor.eController = new EditorControl(p, editor);
	}

	public void updateActive() {
		if (editor.eMode == Editor.editorMode.SELECT) {
			active = true;
		} else {
			active = false;
		}
	}
}
