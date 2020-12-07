package ui;

import camera.FreeCamera;
import controllers.CameraControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class CameraControlWidget extends Widget {
	public CameraControlWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "CameraControls.png");
	}

	public void clicked() {
		if (!active) {
			editor.eController = new CameraControl(p, editor);
			editor.eCamera = new FreeCamera();
		}
	}

	public void updateActive() {
		if (available == true && editor.eController instanceof CameraControl) {
			active = true;
		} else {
			active = false;
		}
	}
}