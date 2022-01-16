package game;

import handlers.TextureCache;
import objects.Rectangle;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.core.PApplet;

import static processing.core.PConstants.*;

public class BackgroundPaper {
	private TextureCache texture;
	int gridSize;

	// rendering algorithm variables
	int startX;
	int startY;
	int endX;
	int endY;
//	PImage image;
//	float leftEdge;
//	float topEdge;
//	float rightEdge;
//	float bottomEdge;
//	int imageStartX;
//	int imageStartY;
//	int imageEndX;
//	int imageEndY;

	PShape shape;

	public BackgroundPaper(PApplet p, TextureCache texture) {
		this.texture = texture;
		gridSize = 1000; // 1000

		shape = p.createShape(RECT, 0, 0, gridSize, gridSize);
		shape.setTexture(texture.getPageViewBackground(1));

	}

	public void draw(PGraphics graphics, PVector topLeft, PVector bottomRight, float scale) {

		// find x start position
		startX = (int) Math.round((topLeft.x - (gridSize / 2)) / gridSize) * gridSize;
		// find y start position
		startY = (int) Math.round((topLeft.y - (gridSize / 2)) / gridSize) * gridSize;
		// find x end position
		endX = (int) Math.round((bottomRight.x + (gridSize / 2)) / gridSize) * gridSize;
		// find y end position
		endY = (int) Math.round((bottomRight.y + (gridSize / 2)) / gridSize) * gridSize;

//		int xTile = 0; // 1;
//		int yTile = 0; // 1;
//		int rightEdge = 0;
//		int bottomEdge = 0;
//
//		for (int x = startX; x < endX; x += gridSize) {
//			xTile += 1;
//			rightEdge = x + gridSize;
//		}
//
//		for (int y = startY; y < endY; y += gridSize) {
//			yTile += 1;
//			bottomEdge = y + gridSize;
//		}
//
//		// texture
//		graphics.noStroke();
//		graphics.textureMode(NORMAL);
//		graphics.beginShape();
//		graphics.textureWrap(REPEAT);
//		graphics.texture(texture.getPageViewBackground(scale));
//		graphics.vertex(startX, startY, 0, 0); // top left
//		graphics.vertex(rightEdge, startY, xTile, 0); // top right
//		graphics.vertex(rightEdge, bottomEdge, xTile, yTile); // bottom right
//		graphics.vertex(startX, bottomEdge, 0, yTile); // bottom left
//		graphics.endShape();

		graphics.pushMatrix();
		graphics.translate(startX, startY);
		
		for (int y = startY; y < endY; y += gridSize) {
			int xDistance = 0;
			
			for (int x = startX; x < endX; x += gridSize) {
				
				graphics.shape(shape);
				
				graphics.translate(gridSize, 0);
				
				xDistance =+ gridSize;
			}
			graphics.translate(-xDistance, gridSize);
		}
		
		graphics.popMatrix();

		// nested for loops to tile the images
//		for (int y = startY; y < endY; y += gridSize) {
//			for (int x = startX; x < endX; x += gridSize) {
				
//				image = texture.getPageViewBackground(scale);
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
//				if (x + gridSize / 2 < topLeft.x) {
//					leftEdge = leftEdge + gridSize / 2;
//					imageStartX = image.width / 2;
//				}
//				if (y + gridSize / 2 < topLeft.y) {
//					topEdge = topEdge + gridSize / 2;
//					imageStartY = image.height / 2;
//				}
//				if (x + gridSize / 2 > bottomRight.x) {
//					rightEdge = rightEdge - gridSize / 2;
//					imageEndX = image.width / 2;
//				}
//				if (y + gridSize / 2 > bottomRight.y) {
//					bottomEdge = bottomEdge - gridSize / 2;
//					imageEndY = image.height / 2;
//				}

//				graphics.imageMode(CORNERS);
//				graphics.image(image, leftEdge, topEdge, rightEdge, bottomEdge, imageStartX, imageStartY, imageEndX,
//						imageEndY);

//				graphics.imageMode(CORNER);
//				graphics.image(texture.getPageViewBackground(scale), x, y, gridSize, gridSize);
//

//			}
//		}

	}

//	public void draw(PGraphics graphics, Rectangle screen, float scale) {
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
//				image = texture.getPageViewBackground(scale);
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
//
//			}
//		}
//	}
}
