package editor.top;

import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class PlayerControlWidget extends Widget {
	public PlayerControlWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "playerControls.png");
	}

	public void clicked() {
		if (!active) {
			editor.controller = new PlayerControl(p, editor.game);
		} else {
			editor.game.restart();
		}
	}

	public void updateActive() {
		if (editor.controller instanceof PlayerControl) {
			active = true;
		} else {
			active = false;
		}
	}
}