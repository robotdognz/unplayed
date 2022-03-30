package editor.uitop;

import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPlayerControl extends Widget {
	
	public WidgetPlayerControl(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "playerControls.png");
	}

	@Override
	public void clicked() {
		if (AppLogic.game.player != null) {
			if (!active) {
				AppLogic.controller = new PlayerControl(p, AppLogic.game);
			} else {
				AppLogic.game.restart();
			}
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (AppLogic.controller instanceof PlayerControl) {
			active = true;
		} else {
			active = false;
		}
	}
}
