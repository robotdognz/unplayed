package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.events.PlayerEnd;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPE180Rotation extends Widget {
	EditorSide toolbar;

	public WidgetPE180Rotation(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "LevelEnd_180sensitive.png");
	}

	@Override
	public void updateActive() {
		super.updateActive();
//		if (editor.selected != null && editor.selected instanceof PlayerEnd) {
//			available = true;
//			if (((PlayerEnd) editor.selected).getRotationMode() == 1) {
//				active = true;
//			} else {
//				active = false;
//			}
//
//		} else {
//			available = false;
//		}

	}

	@Override
	public void clicked() {
//		if (editor.selected != null && editor.selected instanceof PlayerEnd) {
//			if (active) {
//				((PlayerEnd) editor.selected).setRotationMode(0);
//			} else {
//				((PlayerEnd) editor.selected).setRotationMode(1);
//			}
//		}
	}

}