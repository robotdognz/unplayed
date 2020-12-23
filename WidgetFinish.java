package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import game.Game;
import processing.core.PApplet;
import ui.Widget;

public class WidgetFinish extends Widget {
	Game game;
	EditorSide toolbar;

	public WidgetFinish(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		this.game = editor.game;
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "confirmTick.png");
	}

	@Override
	public void clicked() {
		editor.selected = null;
		toolbar.adjust = false;

	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null) {
			available = true;
		} else {
			available = false;
		}
	}
}
