package ui;

import processing.core.PApplet;

public class MenuObject {
	protected float width;
	protected float height;
	
	public MenuObject(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public void draw(PApplet p, float y) {
		
	}
	
	public void drawOnPage(PApplet p, float x, float y) {
		
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
