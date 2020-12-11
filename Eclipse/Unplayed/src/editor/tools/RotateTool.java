package editor.tools;

import java.util.ArrayList;

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
			((Editable) editor.selected).addAngle((p.pmouseX - p.mouseX) / 5);
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

	@Override
	public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRotate(float x, float y, float angle) {
		// TODO Auto-generated method stub
		
	}

}
