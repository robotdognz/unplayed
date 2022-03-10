package editor.uitop;

import editor.Editor;
import editor.EditorSettings;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetCameraLogic extends Widget {
	public WidgetCameraLogic(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "CameraControls.png");
	}

	@Override
	public void clicked() {
		EditorSettings.toggleCameraLogic();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (EditorSettings.cameraLogic()) {
			active = true;
		} else {
			active = false;
		}
	}
}
