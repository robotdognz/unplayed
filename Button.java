package ui;

import static processing.core.PConstants.*;

import handlers.ButtonHandler;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class Button extends MenuObject {
	private float xCenter;
	private float yCenter = 0;
	private String text;
	private boolean hover = false;

	private Rectangle pageButton;

	private ButtonHandler handler = null;

	public Button(float xCenter, float width, float height, String text) {
		super(width, height);
		this.xCenter = xCenter;
		this.text = text;
	}

	public Button(ButtonHandler handler, float xCenter, String text) {
		super(handler.getWidth() * 100, handler.getHeight() * 100);
		this.xCenter = xCenter;
		this.text = text;
		
		PApplet.print("Yup");
	}

	public void setupPageButton(float centerX, float centerY) {// , float width, float height) {
		pageButton = new Rectangle(centerX - width / 2, centerY - height / 2, width, height);
	}

	public void drawOnPage(PApplet p, float x, float y) {
		// can use textWidth() to figure out how wide text is and center it
		float xCenter = x;
		float yCenter = y;
		if (handler == null) { // no sprite
			if (!hover) {
				p.fill(200);
			} else {
				p.fill(100);
			}
			p.rectMode(CENTER);
			p.rect(xCenter, yCenter, width, height);
			p.rectMode(CORNER);
			p.fill(50);
			int textSize = p.width / 24; // 60;
			p.textSize(textSize);
			p.textAlign(CENTER, CENTER);
			p.text(text, xCenter, yCenter);
		} else {
			p.pushMatrix();
			p.translate(xCenter, yCenter);
//			p.scale(size); // size the page will appear in the page view
//			p.rotate(PApplet.radians(angle)); // rotate the page
//			p.scale(flipX, flipY); // flip the page
			p.imageMode(CENTER);
//			p.image(backgroundTexture.getSprite(scale * 0.25f), 0, 0, getWidth(), getHeight()); // draw the page
			p.image(handler.getSprite(0), 0, 0, getWidth(), getHeight()); // draw the button
			p.popMatrix();
		}
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
		p.rect(xCenter, yCenter, width, height);
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
		if (lastTouch.x >= xCenter - width / 2 && lastTouch.y >= yCenter - height / 2
				&& lastTouch.x <= xCenter + width / 2 && lastTouch.y <= yCenter + height / 2) {
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