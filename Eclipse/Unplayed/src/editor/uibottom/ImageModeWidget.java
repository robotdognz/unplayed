package editor.uibottom;

import editor.Editor;
import editor.Toolbar;
import editor.tools.ImageTool;
import processing.core.PApplet;
import ui.Widget;

public class ImageModeWidget extends Widget {
	public ImageModeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "image.png");
		imageInactive = null;
	}

	public void clicked() {
		editor.currentTool = new ImageTool(editor);
	}

	public void updateActive() {
		if (editor.currentTool instanceof ImageTool) {
			active = true;
		} else {
			active = false;
		}
	}
}