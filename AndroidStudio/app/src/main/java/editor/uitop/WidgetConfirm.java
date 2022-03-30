package editor.uitop;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import processing.core.PApplet;
import processing.core.PVector;
import ui.Widget;

public class WidgetConfirm extends Widget {
	public WidgetConfirm(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "confirmTick.png");
		closeAfterSubWidget = true;
	}

	@Override
	public void clicked() {
		// Finalize block
		if(editor.currentTool != null) {
			editor.currentTool.touchMoved(new PVector()); //TODO: this is a bit of a weird way to do it
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (AppLogic.controller instanceof EditorControl && !editor.snap && editor.point != null) {
			available = true;
		} else {
			available = false;
		}
	}
}
