package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import game.Game;
import processing.core.PApplet;
import ui.Widget;

public class WidgetRestart extends Widget {
	Game game;

	public WidgetRestart(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "ResetGame.png");
		closeAfterSubWidget = true;
		game = editor.game;
	}

	@Override
	public void clicked() {
		game.startGame();

	}

	@Override
	public void updateActive() {
		if (game.player != null) {
			available = true;
		} else {
			available = false;
		}
	}

}
