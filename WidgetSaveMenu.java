package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSaveMenu extends Widget {
	public WidgetSaveMenu(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "file.png");
		Widget w1 = new WidgetNew(p, editor, parent);
		Widget w2 = new WidgetSave(p, editor, parent);
		Widget w3 = new WidgetSaveAs(p, editor, parent);
		Widget w4 = new WidgetLoad(p, editor, parent);
		subWidgets.add(w1);
		subWidgets.add(w2);
		subWidgets.add(w3);
		subWidgets.add(w4);
	}
}