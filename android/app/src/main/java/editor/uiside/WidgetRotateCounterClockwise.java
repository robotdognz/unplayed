package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetRotateCounterClockwise extends Widget {
	EditorSide toolbar;

	public WidgetRotateCounterClockwise(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "rotateCounterClockwise.png");
	}

	@Override
	public void updateActive() {

	}

	@Override
	public void clicked() {
		toolbar.addAngle(-90);
	}

}
