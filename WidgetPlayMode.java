package editor.uitop;

import camera.FreeCamera;
import camera.GameCamera;
import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import game.Game;
import game.PageView;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPlayMode extends Widget {
	boolean previousStatus = false;
	Game game;
	PageView pageView;

	public WidgetPlayMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "Pause.png");
		closeAfterSubWidget = true;
		game = editor.game;
		pageView = game.getPageView();
	}

	@Override
	public void clicked() {
		if (active) {
			editor.camera = new FreeCamera();
		} else {
			editor.camera = new GameCamera();
			editor.controller = new PlayerControl(p, editor.game);
			if (!editor.showPageView) { //if we are not on the page view
				editor.switchView();
			}
			game.startGame(editor.showPageView);
			editor.selected = null;
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (game.player != null && pageView.getPages().size() > 0) {
			available = true;
		} else {
			available = false;
			return;
		}
		if (editor.camera instanceof FreeCamera || !editor.showPageView) {
			active = false;
			if (editor.camera instanceof GameCamera) {
				editor.camera = new FreeCamera();
			}
		} else {
			active = true;
		}
	}
}
