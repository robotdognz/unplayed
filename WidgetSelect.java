package editor.uitop;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSelect extends Widget {
	public WidgetSelect(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "select.png");
	}

	public void clicked() {
		editor.eMode = Editor.editorMode.SELECT;
		editor.controller = new EditorControl(p, editor);
	}

	public void updateActive() {
		super.updateActive();
		if (editor.eMode == Editor.editorMode.SELECT) {
			active = true;
		} else {
			active = false;
		}
	}
}
