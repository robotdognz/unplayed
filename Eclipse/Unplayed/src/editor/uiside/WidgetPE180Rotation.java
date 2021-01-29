package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.events.PlayerEnd;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPE180Rotation extends Widget {
	EditorSide toolbar;

	public WidgetPE180Rotation(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "LevelEnd_180sensitive.png");
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