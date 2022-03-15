package editor.uitop;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetAdd extends Widget {
	public WidgetAdd(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "PlaceBlock.png");
	}

	@Override
	public void clicked() {
		editor.eMode = Editor.editorMode.ADD;
		editor.controller = new EditorControl(p, editor);

		// new code, for use when drop-down menu is enabled
		editor.editorSide.clearExternalModes();
		
		editor.nextTouchInactive();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.eMode == Editor.editorMode.ADD) {
			active = true;
		} else {
			active = false;
		}
	}
}