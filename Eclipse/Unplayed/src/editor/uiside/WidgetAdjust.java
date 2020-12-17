package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Page;
import objects.events.PlayerEnd;
import processing.core.PApplet;
import ui.Widget;

public class WidgetAdjust extends Widget {
	EditorSide toolbar;

	public WidgetAdjust(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "rotateClockwise.png");
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && (editor.selected instanceof Page || editor.selected instanceof PlayerEnd)) {
			available = true;
			if (toolbar.adjust) {
				active = true;
			} else {
				active = false;
			}
		} else {
			available = false;
		}

	}

	@Override
	public void clicked() {
		toolbar.adjust = !toolbar.adjust;
	}

}
