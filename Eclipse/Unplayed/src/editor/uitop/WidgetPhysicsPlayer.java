package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPhysicsPlayer extends Widget {
	public WidgetPhysicsPlayer(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "PLayerIcon.png");
	}

	@Override
	public void clicked() {
		editor.game.physicsPlayer = !editor.game.physicsPlayer;
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.game.physicsPlayer) {
			active = true;
		} else {
			active = false;
		}
	}
}