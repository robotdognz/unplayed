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
		// finalise block
		editor.editWorld();
	}

	public void updateActive() {
		if (editor.eController instanceof EditorControl && !editor.snap && editor.eGame.point != null) {
			available = true;
		} else {
			available = false;
		}
	}
}
