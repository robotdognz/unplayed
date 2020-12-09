package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import editor.uiside.ConfirmWidget;
import processing.core.PApplet;
import ui.Widget;

public class ExtraWidget extends Widget {
	public ExtraWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "extraActions.png");

		Widget w1 = new ConfirmWidget(p, editor, parent);
		Widget w2 = new PlayModeWidget(p, editor, parent);
		Widget w3 = new SnapWidget(p, editor, parent);
		Widget w4 = new DebugWidget(p, editor, parent);
		Widget w5 = new QuadtreeWidget(p, editor, parent);
		Widget w6 = new MenuWidget(p, editor, parent);

		subWidgets.add(w1);
		subWidgets.add(w2);
		subWidgets.add(w3);
		subWidgets.add(w4);
		subWidgets.add(w5);
		subWidgets.add(w6);
	}
}