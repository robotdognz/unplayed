package editor.uitop;

import camera.FreeCamera;
import controllers.CameraControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class CameraControlWidget extends Widget {
	public CameraControlWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "CameraControls.png");
	}

	public void clicked() {
		if (!active) {
			editor.controller = new CameraControl(p, editor);
			editor.camera = new FreeCamera();
		}
	}

	public void updateActive() {
		if (available == true && editor.controller instanceof CameraControl) {
			active = true;
		} else {
			active = false;
		}
	}
}