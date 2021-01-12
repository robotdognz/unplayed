package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.events.CameraChange;
import processing.core.PApplet;
import ui.Widget;

public class WidgetCameraAdd extends Widget {
	EditorSide toolbar;

	public WidgetCameraAdd(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "PlaceBlock.png");
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null) { // if there is something selected
			if (editor.selected instanceof CameraChange) {
				available = true;
//				if (toolbar.adjust) {
//					active = true;
//				} else {
//					active = false;
//				}
			}else {
				available = false;
			}
		} else {
			available = false;
		}

	}

	@Override
	public void clicked() {
	}

}