package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;
import controllers.EditorControl;

public class WidgetErase extends Widget {
	public WidgetErase(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "eraser.png");
	}

	@Override
	public void clicked() {
		editor.eMode = Editor.editorMode.ERASE;
		editor.controller = new EditorControl(p, editor);

		// new code, for use when drop-down menu is enabled
		editor.editorSide.clearExternalModes();

	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.eMode == Editor.editorMode.ERASE) {
			active = true;
		} else {
			active = false;
		}
	}
}
