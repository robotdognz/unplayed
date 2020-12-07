package ui;

import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class PlayerControlWidget extends Widget {
	public PlayerControlWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "playerControls.png");
	}

	public void clicked() {
		if (!active) {
			editor.eController = new PlayerControl(p, editor.eGame);
		} else {
			editor.eGame.restart();
		}
	}

	public void updateActive() {
		if (editor.eController instanceof PlayerControl) {
			active = true;
		} else {
			active = false;
		}
	}
}