package game;

import handlers.TextureCache;
import objects.Rectangle;
import processing.core.PGraphics;
import processing.core.PVector;

import static processing.core.PConstants.*;

public class BackgroundPaper {
	private TextureCache texture;
	int gridSize;

	public BackgroundPaper(TextureCache texture) {
		this.texture = texture;
		gridSize = 1000; // 400
	}
	
	public void draw(PGraphics graphics, PVector topLeft, PVector bottomRight, float scale) {
		// if(convert.getScale() > 30){ // stop drawing paper/tiles at this size
		// return;
		// }
		// find x start position
		int startX = (int) Math.round((topLeft.x - (gridSize / 2)) / gridSize) * gridSize;
		// find y start position
		int startY = (int) Math.round((topLeft.y - (gridSize / 2)) / gridSize) * gridSize;
		// find x end position
		int endX = (int) Math.round((bottomRight.x + (gridSize / 2)) / gridSize) * gridSize;
		// find y end position
		int endY = (int) Math.round((bottomRight.y + (gridSize / 2)) / gridSize) * gridSize;
		// nested for loops to tile the images
		for (int y = startY; y < endY; y += gridSize) {
			for (int x = startX; x < endX; x += gridSize) {
				graphics.imageMode(CORNER);
				graphics.image(texture.getPageViewBackground(), x, y, gridSize, gridSize);
			}
		}
	}

	public void draw(PGraphics graphics, Rectangle screen, float scale) {
		// if(convert.getScale() > 30){ // stop drawing paper/tiles at this size
		// return;
		// }
		// find x start position
		int startX = (int) Math.round((screen.getTopLeft().x - (gridSize / 2)) / gridSize) * gridSize;
		// find y start position
		int startY = (int) Math.round((screen.getTopLeft().y - (gridSize / 2)) / gridSize) * gridSize;
		// find x end position
		int endX = (int) Math.round((screen.getBottomRight().x + (gridSize / 2)) / gridSize) * gridSize;
		// find y end position
		int endY = (int) Math.round((screen.getBottomRight().y + (gridSize / 2)) / gridSize) * gridSize;
		// nested for loops to tile the images
		for (int y = startY; y < endY; y += gridSize) {
			for (int x = startX; x < endX; x += gridSize) {
				graphics.imageMode(CORNER);
				graphics.image(texture.getPageViewBackground(), x, y, gridSize, gridSize);
			}
		}
	}
}
