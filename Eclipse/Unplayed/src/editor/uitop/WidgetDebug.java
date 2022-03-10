package editor.uitop;

import editor.Editor;
import editor.EditorSettings;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetDebug extends Widget {
	public WidgetDebug(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "debugging.png");
		closeAfterSubWidget = false;
	}

	@Override
	public void clicked() {
		EditorSettings.toggleDebugOutput();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (EditorSettings.debugOutput()) {
			active = true;
		} else {
			active = false;
		}
	}
}