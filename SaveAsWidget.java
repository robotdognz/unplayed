package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class SaveAsWidget extends Widget {
	public SaveAsWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		available = false;
		icon = p.loadImage(folder + "saveAs.png");
	}
}
