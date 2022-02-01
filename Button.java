package ui;

import static processing.core.PConstants.*;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class Button {
	private float xCenter;
	private float yCenter = 0;
	private float bWidth, bHeight;
	private String text;
	private boolean hover = false;

	private Rectangle pageButton;

	public Button(float xCenter, float bWidth, float bHeight, String text) {
		this.xCenter = xCenter;
		this.bWidth = bWidth;
		this.bHeight = bHeight;
		this.text = text;
	}

	public void setupPageButton(float centerX, float centerY, float width, float height) {
		pageButton = new Rectangle(centerX - width / 2, centerY - height / 2, width, height);
	}

	public void drawOnPage(PApplet p, float x, float y) {
		// can use textWidth() to figure out how wide text is and center it
		float xCenter = x;
		float yCenter = y;
		if (!hover) {
			p.fill(200);
		} else {
			p.fill(100);
		}
		p.rectMode(CENTER);
		p.rect(xCenter, yCenter, bWidth, bHeight);
		p.rectMode(CORNER);
		p.fill(50);
		int textSize = p.width / 24; // 60;
		p.textSize(textSize);
		p.textAlign(CENTER, CENTER);
		p.text(text, xCenter, yCenter);
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
		int textSize = p.width / 24; // 60;
		p.textSize(textSize);
		p.textAlign(CENTER, CENTER);
		p.text(text, xCenter, yCenter);
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

	public void hoverPage(PVector levelTouch) {
		if (levelTouch.x >= pageButton.getTopLeft().x && levelTouch.y >= pageButton.getTopLeft().y
				&& levelTouch.x <= pageButton.getBottomRight().x && levelTouch.y <= pageButton.getBottomRight().y) {
			hover = true;
		} else {
			hover = false;
		}
	}
}