package editor.uibottom;

import static processing.core.PConstants.CENTER;

import processing.core.PApplet;
import processing.core.PImage;

public class NewViewButton {
	private PImage iconBackgroundOff;
	private PImage iconBackgroundOn;
	private String folder = "ui" + '/' + "widgets" + '/'; // data path of widget icons
	private PImage icon;

	public NewViewButton(PApplet p) {
		iconBackgroundOff = p.loadImage(folder + "inactive.png");
		iconBackgroundOn = p.loadImage(folder + "active.png");
		icon = p.loadImage(folder + "Add.png");
	}

	public void draw(PApplet p, float currentX, float currentY, float objectWidth, boolean selected) {
		p.imageMode(CENTER);
		if (!selected) {
			p.image(iconBackgroundOff, currentX, currentY, objectWidth, objectWidth);
			p.tint(75);
		} else {
			p.image(iconBackgroundOn, currentX, currentY, objectWidth, objectWidth);
		}
		p.image(icon, currentX, currentY, objectWidth, objectWidth);
		p.noTint();
	}
}
