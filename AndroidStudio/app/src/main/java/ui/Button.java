package ui;

import static processing.core.PConstants.*;

import game.AppLogic;
import handlers.ButtonHandler;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class Button extends MenuObject {
	private final float xCenter;
	private float yCenter = 0;
	private final String text;
	private boolean hover = false;

	private Rectangle pageButton;

	private ButtonHandler handler = null;
	private ButtonHandler highlight = null;

	public Button(float xCenter, float width, float height, String text) {
		super(width, height);
		this.xCenter = xCenter;
		this.text = text;

		this.highlight = AppLogic.texture.getButtonList().get(0);
	}

	public Button(ButtonHandler handler, float xCenter, String text) {
		super(handler.getWidth() * 100, handler.getHeight() * 100);
		this.xCenter = xCenter;
		this.text = text;
		this.handler = handler;
		this.highlight = AppLogic.texture.getButtonList().get(0);
	}

	public void setupPageButton(float centerX, float centerY) {// , float width, float height) {
		pageButton = new Rectangle(centerX - width / 2, centerY - height / 2, width, height);
	}

	public void drawOnPage(PApplet p, float x, float y) {
		
		if (handler == null) { // no sprite
			p.stroke(100); // 200
			p.noFill();
			p.strokeWeight(5);
			p.rectMode(CENTER);
			p.rect(x, y, width, height);
			p.rectMode(CORNER);
			p.fill(100); // 50
			int textSize = p.width / 24;
			p.textSize(textSize);
			p.textAlign(CENTER, CENTER);
			p.text(text, x, y);
			if (hover) {
				p.blendMode(MULTIPLY); // render it multiplied
				p.pushMatrix();
				p.translate(x, y);
				p.imageMode(CENTER);
				highlight.draw(p.g, 0, 0, getWidth(), getHeight(), 8); // draw the button highlight
				p.popMatrix();
				p.blendMode(BLEND); // back to normal rendering
			}

		} else {
			p.blendMode(MULTIPLY); // render it multiplied
			p.pushMatrix();
			p.translate(x, y);
			p.imageMode(CENTER);
			handler.draw(p.g, 0, 0, getWidth(), getHeight(), 8); // draw the button
			if (hover) {
				highlight.draw(p.g, 0, 0, getWidth(), getHeight(), 8); // draw the button highlight
			}
			p.popMatrix();
			p.blendMode(BLEND); // back to normal rendering
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
		// using a boolean statement to set the variable instead of an if/else
		hover = lastTouch.x >= xCenter - width / 2 && lastTouch.y >= yCenter - height / 2
				&& lastTouch.x <= xCenter + width / 2 && lastTouch.y <= yCenter + height / 2;
	}

	public void hoverPage(PVector levelTouch) {
		// using a boolean statement to set the variable instead of an if/else
		hover = levelTouch.x >= pageButton.getTopLeft().x && levelTouch.y >= pageButton.getTopLeft().y
				&& levelTouch.x <= pageButton.getBottomRight().x && levelTouch.y <= pageButton.getBottomRight().y;
	}
}
