package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetRotateClockwise extends Widget {
	EditorSide toolbar;

	public WidgetRotateClockwise(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "rotateClockwise.png");
	}

	@Override
	public void updateActive() {

	}

	@Override
	public void clicked() {
		toolbar.addAngle();
	}

}
