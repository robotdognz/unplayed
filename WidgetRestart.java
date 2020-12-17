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
		// pageView = game.getPageView();
	}

	@Override
	public void clicked() {
//		editor.camera = new GameCamera();
//		editor.controller = new PlayerControl(p, editor.game);
		game.startGame();
//		editor.showPageView = true;
//		editor.selected = null;

	}

	@Override
	public void updateActive() {

	}

}
