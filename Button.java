package ui;

import static processing.core.PConstants.*;

import processing.core.PApplet;
import processing.core.PVector;

public class Button {
	// private PVector bCenter;
	private float xCenter;
	private float yCenter = 0;
	private float bWidth, bHeight;
	private String text;
	private boolean hover = false;

	public Button(float xCenter, float bWidth, float bHeight, String text) {
		this.xCenter = xCenter;
		this.bWidth = bWidth;
		this.bHeight = bHeight;
		this.text = text;
	}

	public void draw(PApplet p, float y) {
		// can use textWidth() to figure out how wide text is and center it
		yCenter = y;
		if (!hover) {
			p.fill(200);
		} else {
			p.fill(100);
		}
		p.rectMode(CENTER);
		p.rect(xCenter, yCenter, bWidth, bHeight);
		p.rectMode(CORNER);
		p.fill(50);
		int textSize = p.width/24; //60;
		p.textSize(textSize); 
		p.textAlign(CENTER, CENTER);
		p.text(text, xCenter, yCenter);
		// text(text, bCenter.x-bWidth/2, bCenter.y-bHeight/2, bCenter.x+bWidth/2,
		// bCenter.y+bHeight/2);
	}

	public String click() {
		if (hover) {
			return text;
		} else {
			return "";
		}
	}

	public void hover(PVector lastTouch) {
		if (lastTouch.x >= xCenter - bWidth / 2 && lastTouch.y >= yCenter - bHeight / 2
				&& lastTouch.x <= xCenter + bWidth / 2 && lastTouch.y <= yCenter + bHeight / 2) {
			hover = true;
		} else {
			hover = false;
		}
	}
}