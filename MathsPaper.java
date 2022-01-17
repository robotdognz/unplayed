package game;

import handlers.TextureCache;
import objects.Rectangle;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import static processing.core.PConstants.*;

public class MathsPaper {
	private TextureCache texture;
	int gridSize;

	// rendering algorithm variables
//	int startX;
//	int startY;
//	int endX;
//	int endY;
//	PImage image;
//	float leftEdge;
//	float topEdge;
//	float rightEdge;
//	float bottomEdge;
//	int imageStartX;
//	int imageStartY;
//	int imageEndX;
//	int imageEndY;

	public MathsPaper(TextureCache texture) {
		this.texture = texture;
		gridSize = 400;
	}

	public void draw(PGraphics graphics, Rectangle screen, float scale) {
		// TODO: this could be made more efficient for pages because they draw the same
		// every time, the numbers shouldn't be recalculated each frame

		// find x start position
		float startX = (int) Math.round((screen.getTopLeft().x - (gridSize / 2)) / gridSize);
		// find y start position;
		float startY = (int) Math.round((screen.getTopLeft().y - (gridSize / 2)) / gridSize);
		// find x end position
		float endX = (int) Math.round((screen.getBottomRight().x + (gridSize / 2)) / gridSize);
		// find y end position
		float endY = (int) Math.round((screen.getBottomRight().y + (gridSize / 2)) / gridSize);

		float xTileStart = 0; // where to start horizontal tiling in texture units
		float yTileStart = 0; // where to start vertical tiling in texture units
		float xTileEnd = endX - startX; // where to end horizontal tiling in texture units
		float yTileEnd = endY - startY; // where to end vertical tiling in texture units

		// convert to level dimensions
		startX = startX * gridSize;
		startY = startY * gridSize;
		endX = endX * gridSize;
		endY = endY * gridSize;

		// these probably don't need to be if statements, they will always be true
		if (startX < screen.getTopLeft().x) {
			// offset start by difference between startX and topLeft.x in texture units
			xTileStart += (screen.getTopLeft().x - startX) / gridSize;
			// move rectangle left edge over to screen start x
			startX = screen.getTopLeft().x;
		}
		if (startY < screen.getTopLeft().y) {
			// offset start by difference between startY and topLeft.y in texture units
			yTileStart += (screen.getTopLeft().y - startY) / gridSize;
			// move rectangle top edge over to screen start y
			startY = screen.getTopLeft().y;
		}
		if (endX > screen.getBottomRight().x) {
			// offset start by difference between endX and bottomRight.x in texture units
			xTileEnd -= (endX - screen.getBottomRight().x) / gridSize;
			// move rectangle right edge over to screen end x
			endX = screen.getBottomRight().x;
		}
		if (endY > screen.getBottomRight().y) {
			// offset start by difference between endY and bottomRight.y in texture units
			yTileEnd -= (endY - screen.getBottomRight().y) / gridSize;
			// move rectangle right edge over to screen end y
			endY = screen.getBottomRight().y;
		}

		// texture
		graphics.noStroke();
		graphics.textureMode(NORMAL);
		graphics.beginShape();
		graphics.textureWrap(REPEAT);
		graphics.texture(texture.getGrid(scale));
		graphics.vertex(startX, startY, xTileStart, yTileStart); // top left
		graphics.vertex(endX, startY, xTileEnd, yTileStart); // top right
		graphics.vertex(endX, endY, xTileEnd, yTileEnd); // bottom right
		graphics.vertex(startX, endY, xTileStart, yTileEnd); // bottom left
		graphics.endShape();

	}

//	public void draw(PGraphics graphics, Rectangle screen, float scale) {
//
//		// find x start position
//		startX = (int) Math.round((screen.getTopLeft().x - (gridSize / 2)) / gridSize) * gridSize;
//		// find y start position
//		startY = (int) Math.round((screen.getTopLeft().y - (gridSize / 2)) / gridSize) * gridSize;
//		// find x end position
//		endX = (int) Math.round((screen.getBottomRight().x + (gridSize / 2)) / gridSize) * gridSize;
//		// find y end position
//		endY = (int) Math.round((screen.getBottomRight().y + (gridSize / 2)) / gridSize) * gridSize;
//		
//		// nested for loops to tile the images
//		for (int y = startY; y < endY; y += gridSize) {
//			for (int x = startX; x < endX; x += gridSize) {
//
//				image = texture.getGrid(scale);
//
//				leftEdge = x;
//				topEdge = y;
//				rightEdge = x + gridSize;
//				bottomEdge = y + gridSize;
//
//				imageStartX = 0;
//				imageStartY = 0;
//				imageEndX = image.width;
//				imageEndY = image.height;
//
//				if (x < screen.getTopLeft().x) {
//					leftEdge = screen.getTopLeft().x;
//					imageStartX = (int) (image.width * ((screen.getTopLeft().x - x) / gridSize));
//				}
//				if (y < screen.getTopLeft().y) {
//					topEdge = screen.getTopLeft().y;
//					imageStartY = (int) (image.height * ((screen.getTopLeft().y - y) / gridSize));
//				}
//				if (x + gridSize > screen.getBottomRight().x) {
//					rightEdge = screen.getBottomRight().x;
//					imageEndX = (int) (image.width
//							- (image.width * (((x + gridSize) - screen.getBottomRight().x) / gridSize)));
//				}
//				if (y + gridSize > screen.getBottomRight().y) {
//					bottomEdge = screen.getBottomRight().y;
//					imageEndY = (int) (image.height
//							- (image.height * (((y + gridSize) - screen.getBottomRight().y) / gridSize)));
//				}
//
//				graphics.imageMode(CORNERS);
//				graphics.image(image, leftEdge, topEdge, rightEdge, bottomEdge, imageStartX, imageStartY, imageEndX,
//						imageEndY);
//			}
//		}
//	}
}
