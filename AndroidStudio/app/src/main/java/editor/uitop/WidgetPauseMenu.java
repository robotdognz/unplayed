package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import game.Game;
import game.AppLogic;
import processing.core.PApplet;
import ui.EditorMenu;
import ui.GameMenu;
import ui.Widget;

public class WidgetPauseMenu extends Widget {
	private boolean previousStatus = false;

	public WidgetPauseMenu(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "exit.png");
	}

	public WidgetPauseMenu(PApplet p, Game game, Toolbar parent) {
		super(p, null, parent);
		icon = p.loadImage(folder + "exit.png");
	}

	@Override
	public void clicked() {
		if (!active) {
			active = true;
			if (AppLogic.editor != null) {
				AppLogic.addMenu(new EditorMenu(p));
			} else {
				AppLogic.addMenu(new GameMenu(p, AppLogic.game));
			}
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (AppLogic.hasMenu()) {
			active = true;
		} else {
			active = false;
		}
	}

	public boolean getPreviousStatus() {
		return previousStatus;
	}
}
