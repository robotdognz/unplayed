package editor.tools;

import editor.Editor;
import editor.Tool;
import processing.core.PApplet;
import processing.core.PVector;

public class SizeTool implements Tool {
	protected PApplet p;
	protected Editor editor;

	public SizeTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
	}

	@Override
	public void touchMoved() {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchEnded(PVector touch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
