package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSave extends Widget {
	public WidgetSave(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "save.png");
	}

	public void clicked() {
		// save the level
		editor.eJSON.save(editor);
	}
}
