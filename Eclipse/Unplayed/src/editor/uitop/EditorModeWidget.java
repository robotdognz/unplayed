package editor.uitop;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class EditorModeWidget extends Widget {
	public EditorModeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "PlaceBlock.png");
		iconIsCurrentSubWidget = true;
		Widget w1 = new AddWidget(p, editor, parent);
		Widget w2 = new EraseWidget(p, editor, parent);
		Widget w3 = new SelectWidget(p, editor, parent);
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

	public void updateActiveUser() {
		if (editor.controller instanceof EditorControl) {
			active = true;
		} else {
			active = false;
		}
	}
}
