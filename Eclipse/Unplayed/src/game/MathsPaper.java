package game;

import handlers.TextureCache;
import objects.Rectangle;
import processing.core.PGraphics;
import processing.core.PImage;

import static processing.core.PConstants.*;

public class MathsPaper {
	// private PImage grid;
	private TextureCache texture;
	int gridSize;

	public MathsPaper(TextureCache texture) {
		// grid = texture.grid;
		this.texture = texture;
		gridSize = 400;
	}

	public void draw(PGraphics graphics, Rectangle screen, float scale) {

		graphics.imageMode(CORNER);

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

				PImage image = texture.getGrid(scale);

				float leftEdge = x;
				float topEdge = y;
				float rightEdge = x + gridSize;
				float bottomEdge = y + gridSize;

				int imageStartX = 0;
				int imageStartY = 0;
				int imageEndX = image.width;
				int imageEndY = image.height;

				if (x < screen.getTopLeft().x) {
					leftEdge = screen.getTopLeft().x;
					imageStartX = (int) (image.width * ((screen.getTopLeft().x - x) / gridSize));
				}
				if (y < screen.getTopLeft().y) {
					topEdge = screen.getTopLeft().y;
					imageStartY = (int) (image.height * ((screen.getTopLeft().y - y) / gridSize));
				}

				graphics.imageMode(CORNERS);
				graphics.image(image, leftEdge, topEdge, rightEdge, bottomEdge, imageStartX, imageStartY, imageEndX,
						imageEndY);
//				graphics.image(texture.getGrid(scale), x, y, gridSize, gridSize);
			}
		}

		// draw partial image
		// p.image(pageGraphics, 0, 0, pageGraphics.width/2, pageGraphics.height, 0, 0,
		// pageGraphics.width/2, pageGraphics.height);

	}
}
