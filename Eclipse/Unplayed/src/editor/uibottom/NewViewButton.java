package editor.uibottom;

import static processing.core.PConstants.CENTER;

import processing.core.PApplet;
import processing.core.PImage;

public class NewViewButton {
	private PImage iconBackground;
	private String folder = "ui" + '/' + "widgets" + '/'; // data path of widget icons
	private PImage icon;
	
	public NewViewButton(PApplet p) {
		iconBackground = p.loadImage(folder + "inactive.png");
		icon = p.loadImage(folder + "Add.png");
	}
	
	public void draw(PApplet p, float currentX, float currentY, float objectWidth) {
			p.imageMode(CENTER);
			p.image(iconBackground, currentX, currentY, objectWidth,
					objectWidth);
			p.tint(75);
			p.image(icon, currentX, currentY, objectWidth, objectWidth);
			p.noTint();
	}
}
