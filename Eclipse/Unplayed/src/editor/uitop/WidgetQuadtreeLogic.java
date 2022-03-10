package editor.uitop;

import editor.Editor;
import editor.EditorSettings;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetQuadtreeLogic extends Widget {
	public WidgetQuadtreeLogic(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "quadTree.png");
	}

	@Override
	public void clicked() {
		EditorSettings.toggleQuadTreeLogic();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (EditorSettings.quadTreeLogic()) {
			active = true;
		} else {
			active = false;
		}
	}
}
