package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.events.PlayerEnd;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPENoRotation extends Widget {
	EditorSide toolbar;

	public WidgetPENoRotation(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "LevelEnd_notRotaionsenstive.png");
	}
	
	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof PlayerEnd) {
			available = true;
			
			
		} else {
			available = false;
		}

	}

	@Override
	public void clicked() {

	}

}