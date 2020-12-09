package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSaveAs extends Widget {
	public WidgetSaveAs(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		available = false;
		icon = p.loadImage(folder + "saveAs.png");
	}
}
