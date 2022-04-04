package editor;

import java.util.ArrayList;

import processing.core.PVector;

public interface Tool {
	public void touchMoved(PVector touch);
	public void touchEnded(PVector touch);
	public void onPinch(ArrayList<PVector> touches, float x, float y, float d);
	public void onRotate(float x, float y, float angle);
	public void onTap(float x, float y);
	public void draw();
	public Object getResult();
	
}
