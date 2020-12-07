package controllers;

import java.util.ArrayList;

import processing.core.PVector;

public interface Controller {
	public void step(ArrayList<PVector> touch);

	public void touchStarted(PVector touch);

	public void touchEnded();

	public void touchMoved(ArrayList<PVector> touch);

	public void onPinch(ArrayList<PVector> touch, float x, float y, float d);
}
