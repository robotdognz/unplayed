package handlers;

import java.io.File;

import processing.core.PImage;

public interface Handler {
	public PImage getSprite(float scale);

	public File getFile();

	public int getWidth();

	public int getHeight();

	public void draw(float pX, float pY, float size);
}
