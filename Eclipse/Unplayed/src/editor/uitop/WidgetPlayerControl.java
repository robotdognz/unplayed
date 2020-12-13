package editor.uitop;

import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import game.Game;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPlayerControl extends Widget {
	Game game;
	
	public WidgetPlayerControl(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "playerControls.png");
		this.game = editor.game;
	}

	public void clicked() {
		if (game.player != null) {
			if (!active) {
				editor.controller = new PlayerControl(p, editor.game);
			} else {
				editor.game.restart();
			}
		}
	}

	public void updateActive() {
		super.updateActive();
		if (editor.controller instanceof PlayerControl) {
			active = true;
		} else {
			active = false;
		}
	}
}