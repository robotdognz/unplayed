package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.events.PlayerEnd;
import processing.core.PApplet;
import ui.Widget;

public class WidgetAddSpawn extends Widget {
	EditorSide toolbar;

	public WidgetAddSpawn(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "AddSpawn.png");
	}
	
	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof PlayerEnd) {
			available = true;
			if (toolbar.isLevelEnd()) {
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
		toolbar.levelEnd(!toolbar.isLevelEnd());
	}

}
