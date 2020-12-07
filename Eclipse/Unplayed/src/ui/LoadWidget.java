package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class LoadWidget extends Widget {
	public LoadWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "load.png");
	}

	public void clicked() {
		// load the level
		editor.eJSON.load(editor.eGame);
	}
}