package game;

import handlers.TextureCache;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import static processing.core.PConstants.*;

public class MathsPaper {
    private static final int gridSize = 400;

    public static void draw(PGraphics graphics, Rectangle screen, float scale, int gridMultiply) {
        // TODO: this could be made more efficient for pages because they draw the same
        // every time, the numbers shouldn't be recalculated each frame

        int gridSize = MathsPaper.gridSize / gridMultiply;

        // find x start position
        float startX = (int) Math.round((screen.getTopLeft().x - (gridSize * 0.5f)) / gridSize);
        // find y start position;
        float startY = (int) Math.round((screen.getTopLeft().y - (gridSize * 0.5f)) / gridSize);
        // find x end position
        float endX = (int) Math.round((screen.getBottomRight().x + (gridSize * 0.5f)) / gridSize);
        // find y end position
        float endY = (int) Math.round((screen.getBottomRight().y + (gridSize * 0.5f)) / gridSize);

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
        graphics.blendMode(MULTIPLY); // render it multiplied
//        graphics.tint(255, 107); // equivalent to 42% opacity
        graphics.noStroke();
        graphics.textureMode(NORMAL);
        graphics.beginShape();
        graphics.textureWrap(REPEAT);
        graphics.texture(TextureCache.getGrid(scale));
        graphics.vertex(startX, startY, xTileStart, yTileStart); // top left
        graphics.vertex(endX, startY, xTileEnd, yTileStart); // top right
        graphics.vertex(endX, endY, xTileEnd, yTileEnd); // bottom right
        graphics.vertex(startX, endY, xTileStart, yTileEnd); // bottom left
        graphics.endShape();

        graphics.blendMode(BLEND); // back to normal rendering

    }

    public static PShape makePaper(PApplet p, Rectangle screen, float scale, int gridMultiply) {

		int gridSize = MathsPaper.gridSize / gridMultiply;

		// find x start position
		float startX = (int) Math.round((screen.getTopLeft().x - (gridSize * 0.5f)) / gridSize);
		// find y start position;
		float startY = (int) Math.round((screen.getTopLeft().y - (gridSize * 0.5f)) / gridSize);
		// find x end position
		float endX = (int) Math.round((screen.getBottomRight().x + (gridSize * 0.5f)) / gridSize);
		// find y end position
		float endY = (int) Math.round((screen.getBottomRight().y + (gridSize * 0.5f)) / gridSize);

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

		PShape s;
		// First create the shape
		p.textureMode(NORMAL);
		p.textureWrap(REPEAT);
		s = p.createShape();
		s.noStroke();
		s.beginShape();
		// Here, we are hardcoding a series of vertices
		s.texture(TextureCache.getGrid(scale));
		s.vertex(startX, startY, xTileStart, yTileStart); // top left
		s.vertex(endX, startY, xTileEnd, yTileStart); // top right
		s.vertex(endX, endY, xTileEnd, yTileEnd); // bottom right
		s.vertex(startX, endY, xTileStart, yTileEnd); // bottom left
		s.endShape(CLOSE);

		return s;
    }

}
