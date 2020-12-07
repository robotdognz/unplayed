package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class SaveWidget extends Widget {
	public SaveWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "save.png");
	}

	public void clicked() {
		// save the level
		editor.eJSON.save(editor.game);
	}
}
