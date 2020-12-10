package editor.tools;

import editor.Editor;
import editor.Tool;
import objects.Editable;
import processing.core.PApplet;
import processing.core.PVector;

public class RotateTool implements Tool {
	protected PApplet p;
	protected Editor editor;

	public RotateTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
	}
	
	@Override
	public void touchMoved() {
		if (editor.selected != null && editor.selected instanceof Editable) {
			((Editable) editor.selected).addAngle((p.pmouseX - p.mouseX) / 3);
		}
		
	}

	@Override
	public void touchEnded(PVector touch) {
	}

	@Override
	public void draw() {
	}

	@Override
	public Object getResult() {
		return null;
	}

}
