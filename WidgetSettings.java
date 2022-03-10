package editor.uitop;

import editor.DebugOutput;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSettings extends Widget {
	public WidgetSettings(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "settings.png");

		Widget w1 = new WidgetShowViews(p, editor, parent);
		Widget w2 = new WidgetDebug(p, editor, parent);
		Widget w3 = new WidgetPlayerPhysicsLogic(p, editor, parent);
		Widget w4 = new WidgetCameraLogic(p, editor, parent);
		Widget w5 = new WidgetQuadtreeLogic(p, editor, parent);

		subWidgets.add(w1);
		subWidgets.add(w2);
		subWidgets.add(w3);
		subWidgets.add(w4);
		subWidgets.add(w5);
	}

	@Override
	public void deactivate() {
		DebugOutput.pushMessage("Deactivated settings menu", 1);
		super.deactivate();
	}

	@Override
	public void clicked() {
		DebugOutput.pushMessage("Clicked settings menu", 1);
		super.clicked();
	}
}