package editor.uitop;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import processing.core.PVector;
import ui.Widget;

public class WidgetConfirm extends Widget {
	public WidgetConfirm(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "confirmTick.png");
		closeAfterSubWidget = true;
	}

	public void clicked() {
		// Finalize block
		if(editor.currentTool != null) {
			editor.currentTool.touchMoved(new PVector()); //TODO: this is a bit of a weird way to do it
		}
	}

	public void updateActive() {
		super.updateActive();
		if (editor.controller instanceof EditorControl && !editor.snap && editor.point != null) {
			available = true;
		} else {
			available = false;
		}
	}
}
