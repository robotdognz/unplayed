package editor.uitop;

import camera.FreeCamera;
import controllers.CameraControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetCameraControl extends Widget {
	public WidgetCameraControl(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "CameraControls.png");
	}

	@Override
	public void clicked() {
		if (!active) {
			editor.controller = new CameraControl(p, editor);
			editor.camera = new FreeCamera();
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (available == true && editor.controller instanceof CameraControl) {
			active = true;
		} else {
			active = false;
		}
	}
}
