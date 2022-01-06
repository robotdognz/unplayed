package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetTumble extends Widget {
	public WidgetTumble(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
//		icon = p.loadImage(folder + "debugging.png");
	}

	@Override
	public void clicked() {
		editor.game.tumble = !editor.game.tumble;
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.game.tumble) {
			active = true;
		} else {
			active = false;
		}
	}
}
