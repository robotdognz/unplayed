package ui;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class ConfirmWidget extends Widget {
	public ConfirmWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "confirmTick.png");
		closeAfterSubWidget = true;
	}

	public void clicked() {
		// Finalize block
		editor.editWorld();
	}

	public void updateActive() {
		if (editor.controller instanceof EditorControl && !editor.snap && editor.game.point != null) {
			available = true;
		} else {
			available = false;
		}
	}
}
