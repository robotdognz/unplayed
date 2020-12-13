package editor.uitop;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetEditorMode extends Widget {
	public WidgetEditorMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "PlaceBlock.png");
		iconIsCurrentSubWidget = true;
		Widget w1 = new WidgetAdd(p, editor, parent);
		Widget w2 = new WidgetErase(p, editor, parent);
		Widget w3 = new WidgetSelect(p, editor, parent);
		subWidgets.add(w1);
		subWidgets.add(w2);
		subWidgets.add(w3);

		hasSActive = true;
	}

	public void clicked() {
		if (active == false) {
			active = true;
			editor.controller = new EditorControl(p, editor);
		} else {
			sActive = !sActive;
		}
	}

	public void updateActive() {
		super.updateActive();
		if (editor.controller instanceof EditorControl) {
			active = true;
		} else {
			active = false;
		}
	}
}
