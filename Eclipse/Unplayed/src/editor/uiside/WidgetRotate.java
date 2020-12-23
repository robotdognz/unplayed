package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetRotate extends Widget {
	EditorSide toolbar;

	public WidgetRotate(PApplet p, Editor editor, Toolbar parent) {
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
