package game;

import handlers.TextureCache;
import objects.Rectangle;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import static processing.core.PConstants.*;

public class BackgroundPaper {
	private TextureCache texture;
	int gridSize;

	// rendering algorithm variables
	int startX;
	int startY;
	int endX;
	int endY;
	PImage image;
	float leftEdge;
	float topEdge;
	float rightEdge;
	float bottomEdge;
	int imageStartX;
	int imageStartY;
	int imageEndX;
	int imageEndY;

	public BackgroundPaper(TextureCache texture) {
		this.texture = texture;
		gridSize = 1000; // 1000
	}

	public void draw(PGraphics graphics, PVector topLeft, PVector bottomRight, float scale) {
		
		float topLeftXRounded = (int) Math.floor(topLeft.x);
//		float topLeftYRounded = (int) Math.floor(topLeft.y);
//		float bottomRightXRounded = (int) Math.ceil(bottomRight.x);
//		float bottomRightYRounded = (int) Math.ceil(bottomRight.y);
		
		// find x start position
		startX = (int) Math.round((topLeft.x - (gridSize / 2)) / gridSize) * gridSize;
		// find y start position
		startY = (int) Math.round((topLeft.y - (gridSize / 2)) / gridSize) * gridSize;
		// find x end position
		endX = (int) Math.round((bottomRight.x + (gridSize / 2)) / gridSize) * gridSize;
		// find y end position
		endY = (int) Math.round((bottomRight.y + (gridSize / 2)) / gridSize) * gridSize;
		
		
		// nested for loops to tile the images
		for (int y = startY; y < endY; y += gridSize) {
			for (int x = startX; x < endX; x += gridSize) {

				image = texture.getPageViewBackground(scale);

				leftEdge = x;
				topEdge = y;
				rightEdge = x + gridSize;
				bottomEdge = y + gridSize;

				imageStartX = 0;
				imageStartY = 0;
				imageEndX = image.width;
				imageEndY = image.height;

				if (x < topLeft.x) {
					leftEdge = topLeftXRounded;
					imageStartX = (int) Math.floor(image.width * ((topLeftXRounded - x) / gridSize));
				}
				if (y < topLeft.y) {
					topEdge = topLeft.y;
					imageStartY = (int) (image.height * ((topLeft.y - y) / gridSize));
				}
				if (x + gridSize > bottomRight.x) {
					rightEdge = bottomRight.x;
					imageEndX = (int) (image.width
							- (image.width * (((x + gridSize) - bottomRight.x) / gridSize)));
				}
				if (y + gridSize > bottomRight.y) {
					bottomEdge = bottomRight.y;
					imageEndY = (int) (image.height
							- (image.height * (((y + gridSize) - bottomRight.y) / gridSize)));
				}

				graphics.imageMode(CORNERS);
				graphics.image(image, leftEdge, topEdge, rightEdge, bottomEdge, imageStartX, imageStartY, imageEndX,
						imageEndY);

			}
		}
	}

	public void draw(PGraphics graphics, Rectangle screen, float scale) {
		// find x start position
		startX = (int) Math.round((screen.getTopLeft().x - (gridSize / 2)) / gridSize) * gridSize;
		// find y start position
		startY = (int) Math.round((screen.getTopLeft().y - (gridSize / 2)) / gridSize) * gridSize;
		// find x end position
		endX = (int) Math.round((screen.getBottomRight().x + (gridSize / 2)) / gridSize) * gridSize;
		// find y end position
		endY = (int) Math.round((screen.getBottomRight().y + (gridSize / 2)) / gridSize) * gridSize;
		
		// nested for loops to tile the images
		for (int y = startY; y < endY; y += gridSize) {
			for (int x = startX; x < endX; x += gridSize) {

				image = texture.getPageViewBackground(scale);

				leftEdge = x;
				topEdge = y;
				rightEdge = x + gridSize;
				bottomEdge = y + gridSize;

				imageStartX = 0;
				imageStartY = 0;
				imageEndX = image.width;
				imageEndY = image.height;

				if (x < screen.getTopLeft().x) {
					leftEdge = screen.getTopLeft().x;
					imageStartX = (int) (image.width * ((screen.getTopLeft().x - x) / gridSize));
				}
				if (y < screen.getTopLeft().y) {
					topEdge = screen.getTopLeft().y;
					imageStartY = (int) (image.height * ((screen.getTopLeft().y - y) / gridSize));
				}
				if (x + gridSize > screen.getBottomRight().x) {
					rightEdge = screen.getBottomRight().x;
					imageEndX = (int) (image.width
							- (image.width * (((x + gridSize) - screen.getBottomRight().x) / gridSize)));
				}
				if (y + gridSize > screen.getBottomRight().y) {
					bottomEdge = screen.getBottomRight().y;
					imageEndY = (int) (image.height
							- (image.height * (((y + gridSize) - screen.getBottomRight().y) / gridSize)));
				}

				graphics.imageMode(CORNERS);
				graphics.image(image, leftEdge, topEdge, rightEdge, bottomEdge, imageStartX, imageStartY, imageEndX,
						imageEndY);

			}
		}
	}
}
