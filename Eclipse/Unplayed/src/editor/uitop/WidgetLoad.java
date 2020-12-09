package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetLoad extends Widget {
	public WidgetLoad(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "load.png");
	}

	public void clicked() {
		// load the level
		editor.eJSON.load(editor.game);
	}
}