package ui;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class AddWidget extends Widget {
	public AddWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "PlaceBlock.png");
	}

	public void clicked() {
		editor.eMode = Editor.editorMode.ADD;
		editor.eController = new EditorControl(p, editor);
	}

	public void updateActive() {
		if (editor.eMode == Editor.editorMode.ADD) {
			active = true;
		} else {
			active = false;
		}
	}
}