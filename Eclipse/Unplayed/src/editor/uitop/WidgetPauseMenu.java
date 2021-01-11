package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import game.Game;
import game.AppLogic;
import processing.core.PApplet;
import ui.PauseMenu;
import ui.Widget;

public class WidgetPauseMenu extends Widget {
	private boolean previousStatus = false;
	private Game game;
	private AppLogic app;

	public WidgetPauseMenu(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "exit.png");
		game = editor.game;
		app = game.app;
	}
	
	public WidgetPauseMenu(PApplet p, Game game, Toolbar parent) {
		super(p, null, parent);
		icon = p.loadImage(folder + "exit.png");
		this.game = game;
		app = game.app;
	}

	@Override
	public void clicked() {
		if (!active) {
			active = true;
			previousStatus = app.gPaused;
			app.gPaused = true; // switch pause state
			app.menu = new PauseMenu(p, game, app, this);
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (app.menu != null) {
			active = true;
		}else {
			active = false;
		}
	}
	
	public boolean getPreviousStatus() {
		return previousStatus;
	}
}
