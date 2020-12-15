package editor.uibottom;

import editor.Editor;
import editor.Toolbar;
import editor.tools.ImageTool;
import processing.core.PApplet;
import ui.Widget;

public class WidgetImageMode extends Widget {
	public WidgetImageMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "image.png");
		imageInactive = null;
	}

	@Override
	public void clicked() {
		editor.currentTool = new ImageTool(editor);
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.currentTool instanceof ImageTool) {
			active = true;
		} else {
			active = false;
		}
	}
}