package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class SaveMenuWidget extends Widget {
	public SaveMenuWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "file.png");
		// wd = widgetDirection.LEFT;
		Widget w1 = new SaveWidget(p, editor, parent);
		Widget w2 = new SaveAsWidget(p, editor, parent);
		Widget w3 = new LoadWidget(p, editor, parent);
		subWidgets.add(w1);
		subWidgets.add(w2);
		subWidgets.add(w3);
	}
}