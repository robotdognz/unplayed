package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import processing.core.PApplet;
import ui.Widget;

public class WidgetReset extends Widget {
	public WidgetReset(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "deleteButton.png");
	}

	@Override
	public void clicked() {
		AppLogic.quitPurge(); // exit the game
	}

	@Override
	public void updateActive() {
		super.updateActive();
	}
}
