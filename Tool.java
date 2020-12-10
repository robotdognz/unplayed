package editor;

import processing.core.PVector;

public interface Tool {
	public void touchMoved();
	public void touchEnded(PVector touch);
	public void draw();
	public Object getResult();
}
