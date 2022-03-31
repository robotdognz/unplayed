package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetExtra extends Widget {
	public WidgetExtra(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "extraActions.png");

		Widget w1 = new WidgetPreview(p, editor, parent);
		Widget w2 = new WidgetRestart(p, editor, parent);
//		Widget w3 = new WidgetConfirm(p, editor, parent);
//		Widget w4 = new WidgetSnap(p, editor, parent);
		Widget wSettings = new WidgetSettings(p, editor, parent);

		subWidgets.add(w1);
		subWidgets.add(w2);
		subWidgets.add(wSettings);
	}
}
