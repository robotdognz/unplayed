package editor.uitop;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSelect extends Widget {
	public WidgetSelect(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "select.png");
	}

	@Override
	public void clicked() {
		editor.editorMode = Editor.EditorMode.SELECT;
		AppLogic.controller = new EditorControl(p, editor);

		// new code, for use when drop-down menu is enabled
		editor.editorSide.clearExternalModes();
		
		editor.nextTouchInactive();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.editorMode == Editor.EditorMode.SELECT) {
			active = true;
		} else {
			active = false;
		}
	}
}
