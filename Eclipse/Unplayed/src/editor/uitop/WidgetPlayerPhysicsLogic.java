package editor.uitop;

import editor.Editor;
import editor.EditorSettings;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPlayerPhysicsLogic extends Widget {
	public WidgetPlayerPhysicsLogic(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "PLayerIcon.png");
	}

	@Override
	public void clicked() {
		EditorSettings.togglePlayerLogic();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (EditorSettings.playerLogic()) {
			active = true;
		} else {
			active = false;
		}
	}
}
