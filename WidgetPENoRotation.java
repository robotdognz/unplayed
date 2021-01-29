package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.events.PlayerEnd;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPENoRotation extends Widget {
	EditorSide toolbar;

	public WidgetPENoRotation(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "LevelEnd_notRotaionsenstive.png");
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof PlayerEnd) {
			available = true;
			if (((PlayerEnd) editor.selected).getRotationMode() == 2) {
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
		if (editor.selected != null && editor.selected instanceof PlayerEnd) {
			if (active) {
				((PlayerEnd) editor.selected).setRotationMode(0);
			} else {
				((PlayerEnd) editor.selected).setRotationMode(2);
			}
		}
	}

}