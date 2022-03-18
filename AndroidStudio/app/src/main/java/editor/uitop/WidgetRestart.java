package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import processing.core.PApplet;
import ui.Widget;

public class WidgetRestart extends Widget {

	public WidgetRestart(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "ResetGame.png");
		closeAfterSubWidget = true;
	}

	@Override
	public void clicked() {
		AppLogic.game.startGame();

	}

	@Override
	public void updateActive() {
		if (AppLogic.game.player != null) {
			available = true;
		} else {
			available = false;
		}
	}

}
