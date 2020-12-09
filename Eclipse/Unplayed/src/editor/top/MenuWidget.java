package editor.top;

import editor.Editor;
import editor.Toolbar;
import game.Game;
import game.GameLogic;
import processing.core.PApplet;
import ui.PauseMenu;
import ui.Widget;

public class MenuWidget extends Widget {
	private boolean previousStatus = false;
	private Game game;
	private GameLogic gl;

	public MenuWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "exit.png");
		game = editor.game;
		gl = game.gl;
	}

	public void clicked() {
		if (!active) {
			active = true;
			previousStatus = gl.gPaused;
			gl.gPaused = true; // switch pause state
			gl.menu = new PauseMenu(p, game, gl, this);
		}
	}

	public void updateActive() {
		if (gl.menu != null) {
			active = true;
		}
	}
	
	public boolean getPreviousStatus() {
		return previousStatus;
	}
}
