package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class ImageModeWidget extends Widget {
	public ImageModeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "image.png");
		imageInactive = null;
	}

	public void clicked() {
		editor.eType = Editor.editorType.IMAGE;
	}

	public void updateActive() {
		if (editor.eType == Editor.editorType.IMAGE) {
			active = true;
		} else {
			active = false;
		}
	}
}