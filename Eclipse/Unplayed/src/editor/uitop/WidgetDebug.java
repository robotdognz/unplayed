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
	}

	@Override
	public void clicked() {
//		editor.debugVis = !editor.debugVis;
		EditorSettings.toggleDebugOutput();
	}

	@Override
	public void updateActive() {
		super.updateActive();
//		if (editor.debugVis) {
//			active = true;
//		} else {
//			active = false;
//		}
		if (EditorSettings.debugOutput()) {
			active = true;
		} else {
			active = false;
		}
	}
}