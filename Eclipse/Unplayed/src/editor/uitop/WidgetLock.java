package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetLock extends Widget {
	public WidgetLock(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
//		icon = p.loadImage(folder + "debugging.png");
	}

	@Override
	public void clicked() {
		editor.game.locked = !editor.game.locked;
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.game.locked) {
			active = true;
		} else {
			active = false;
		}
	}
}
