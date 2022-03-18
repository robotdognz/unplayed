package game;

import handlers.TextureCache;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.core.PApplet;

import static processing.core.PConstants.*;

public class BackgroundPaper {
	int gridSize;

	public BackgroundPaper(PApplet p) {
		gridSize = 1100;

	}

	public void draw(PGraphics graphics, PVector topLeft, PVector bottomRight, float scale) {
		// TODO: this could potentially be made more efficient by only recalculating
		// when the screen has moved

		// find x start position
		float startX = (int) Math.round((topLeft.x - (gridSize / 2)) / gridSize);
		// find y start position;
		float startY = (int) Math.round((topLeft.y - (gridSize / 2)) / gridSize);
		// find x end position
		float endX = (int) Math.round((bottomRight.x + (gridSize / 2)) / gridSize);
		// find y end position
		float endY = (int) Math.round((bottomRight.y + (gridSize / 2)) / gridSize);

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
		if (startX < topLeft.x) {
			// offset start by difference between startX and topLeft.x in texture units
			xTileStart += (topLeft.x - startX) / gridSize;
			// move rectangle left edge over to screen start x
			startX = topLeft.x;
		}
		if (startY < topLeft.y) {
			// offset start by difference between startY and topLeft.y in texture units
			yTileStart += (topLeft.y - startY) / gridSize;
			// move rectangle top edge over to screen start y
			startY = topLeft.y;
		}
		if (endX > bottomRight.x) {
			// offset start by difference between endX and bottomRight.x in texture units
			xTileEnd -= (endX - bottomRight.x) / gridSize;
			// move rectangle right edge over to screen end x
			endX = bottomRight.x;
		}
		if (endY > bottomRight.y) {
			// offset start by difference between endY and bottomRight.y in texture units
			yTileEnd -= (endY - bottomRight.y) / gridSize;
			// move rectangle right edge over to screen end y
			endY = bottomRight.y;
		}

		// texture
		graphics.noStroke();
		graphics.textureMode(NORMAL);
		graphics.beginShape();
		graphics.textureWrap(REPEAT);
		graphics.texture(TextureCache.getPageViewBackground(scale));
		graphics.vertex(startX, startY, xTileStart, yTileStart); // top left
		graphics.vertex(endX, startY, xTileEnd, yTileStart); // top right
		graphics.vertex(endX, endY, xTileEnd, yTileEnd); // bottom right
		graphics.vertex(startX, endY, xTileStart, yTileEnd); // bottom left
		graphics.endShape();

	}

}
