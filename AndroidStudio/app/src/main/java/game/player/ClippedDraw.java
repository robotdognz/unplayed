package game.player;

import static processing.core.PConstants.*;

import org.jbox2d.common.Vec2;

import game.AppLogic;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PGraphics;

public class ClippedDraw {
    // player drawing algorithm
    private static PGraphics canvas;
    private static PGraphics mask;

    public ClippedDraw(PApplet p) {
        // create player drawer
        int renderResolution = 256;
        canvas = p.createGraphics(renderResolution, renderResolution, P2D);
        mask = p.createGraphics(renderResolution, renderResolution, P2D);

        canvas = p.createGraphics(renderResolution, renderResolution, P2D);
        mask = p.createGraphics(renderResolution, renderResolution, P2D);
    }

    /**
     * Renders the player clipped to a specific region.
     * Unlike the drawPlayerComplete(), this method uses an optimised version of the
     * algorithm that is only able to render rectangles. The player that gets drawn
     * will have a slightly unclean edge.
     *
     * @param graphics     the graphics object to draw the output to
     * @param clippingArea the area used to clip the player
     * @param scale        the LOD to use
     */
    public static void drawPlayerOptimised(PGraphics graphics, Rectangle clippingArea, float scale) {
        Player playerCube = AppLogic.game.player;
        if (playerCube == null) {
            return;
        }
        float objectAngle = playerCube.getDrawingAngle();
        Vec2 objectCenter = playerCube.getCenter();

        // draw the mask at the player position and add masking

        mask.beginDraw();
        mask.background(0); // black
        mask.rectMode(CORNER);
        mask.translate(mask.width * 0.5f, mask.height * 0.5f); // set to center

        float xDiff = clippingArea.getX() - objectCenter.x;
        float yDiff = clippingArea.getY() - objectCenter.y;

        mask.noStroke();
        mask.rotate(objectAngle);
        mask.scale(256 / 100f);
        mask.fill(255); // white
        mask.rect(xDiff, yDiff, clippingArea.getWidth(), clippingArea.getHeight());
        mask.endDraw();

        // draw the player

        canvas.beginDraw();
        canvas.translate(mask.width * 0.5f, mask.height * 0.5f); // set to center
        canvas.background(240, 0);
        canvas.scale(256 / 100f);
        playerCube.drawNoTransform(canvas, scale);
        canvas.endDraw();

        canvas.mask(mask);

        graphics.pushMatrix();
        graphics.imageMode(CENTER);
        graphics.translate(objectCenter.x, objectCenter.y);
        graphics.rotate(-objectAngle);
        graphics.scale(100 / 256f);
        graphics.image(canvas, 0, 0);
        graphics.popMatrix();
    }

    /**
     * Renders the player clipped to a specific region.
     * Unlike the drawPlayerSimple() method, this method uses the full version of the
     * rendering algorithm, same as drawTransition(). This gives the rendered player a
     * cleaner edge.
     *
     * @param graphics     the graphics object to draw the output to
     * @param clippingArea the area used to clip the player
     * @param scale        the LOD to use
     */
    public static void drawPlayerComplete(PGraphics graphics, Rectangle clippingArea, float scale) {
        Player playerCube = AppLogic.game.player;
        if (playerCube == null) {
            return;
        }
        float objectAngle = playerCube.getDrawingAngle();
        Vec2 objectCenter = playerCube.getCenter();

        // draw the mask at the player position and add masking

        // set the mask PImage background to white
        mask.beginDraw();
        mask.blendMode(BLEND);
        mask.background(0); // white

        // translate to middle of mask PImage
        mask.translate(mask.width * 0.5f, mask.height * 0.5f); // set to center

        // calculate the amount to offset the clipping area by so it correctly overlaps the mask
        float xDiff = clippingArea.getX() - objectCenter.x;
        float yDiff = clippingArea.getY() - objectCenter.y;

        // draw the clipping area rectangle over the mask in black
        mask.noStroke();
        mask.rotate(objectAngle);
        mask.scale(256 / 100f);
        mask.fill(255); // black
        mask.rectMode(CORNER);
        mask.rect(xDiff, yDiff, clippingArea.getWidth(), clippingArea.getHeight());

        // draw the sprite to be masked onto the mask PImage, tinted to black
        // this is used to determine what area of the mask will be used to draw the final sprite
        mask.tint(0);
        mask.rotate(-objectAngle);
        playerCube.drawNoTransform(mask, scale);
        mask.noTint();

        // draw the clipping area rectangle over the mask again but in white using EXCLUSION blend mode
        mask.blendMode(EXCLUSION);
        mask.fill(255); // white
        mask.rectMode(CORNER);
        mask.rotate(objectAngle);
        mask.rect(xDiff, yDiff, clippingArea.getWidth(), clippingArea.getHeight());

        // reset the blend mode to normal, invert the mask PImage, and end drawing
        mask.blendMode(BLEND);
        mask.endDraw();

        // draw the player

        canvas.beginDraw();
        canvas.translate(mask.width * 0.5f, mask.height * 0.5f); // set to center
        canvas.background(240, 0);
        canvas.scale(256 / 100f);
        playerCube.drawNoTransform(canvas, scale);
        canvas.endDraw();

        canvas.mask(mask);

        graphics.pushMatrix();
        graphics.imageMode(CENTER);
        graphics.translate(objectCenter.x, objectCenter.y);
        graphics.rotate(-objectAngle);
        graphics.scale(100 / 256f);
        graphics.image(canvas, 0, 0);
        graphics.popMatrix();
    }

    /**
     * Renders the player transition clipped to a specific region.
     *
     * @param graphics     the graphics object to draw the output to
     * @param clippingArea the area used to clip the transition
     * @param scale        the LOD to use
     */
    public static void drawTransition(PGraphics graphics, Rectangle clippingArea, float scale) {
        Vec2 objectCenter = AppLogic.game.playerTransition.getCenter();
        float size = AppLogic.game.playerTransition.getSize();

        Vec2 start = AppLogic.game.playerTransition.getStart();
        Vec2 end = AppLogic.game.playerTransition.getEnd();
        float tileSize = 100;

        // draw the mask ot the transition effect position and add masking

        // set the mask PImage background to white
        mask.beginDraw();
        mask.blendMode(BLEND);
        mask.background(0); // white
        // translate to middle of mask PImage
        mask.translate(mask.width * 0.5f, mask.height * 0.5f); // set to center
        // calculate the amount to offset the clipping area by so it correctly overlaps the mask
        float clippingAreaOffsetX = clippingArea.getX() - objectCenter.x;
        float clippingAreaOffsetY = clippingArea.getY() - objectCenter.y;
        // draw the clipping area rectangle over the mask in black
        mask.noStroke();
        mask.scale(256 / 100f);
        mask.fill(255); // black
        mask.rectMode(CORNER);
        mask.rect(clippingAreaOffsetX, clippingAreaOffsetY, clippingArea.getWidth(), clippingArea.getHeight());
        // draw the sprite to be masked onto the mask PImage, tinted to black
        // this is used to determine what area of the mask will be used to draw the final sprite
        mask.fill(0);
        mask.rectMode(CENTER);
        mask.rect(0, 0, size, size); // TODO: replace with actual sprite and tinting
        // draw the clipping area rectangle over the mask again but in white using EXCLUSION blend mode
        mask.blendMode(EXCLUSION);
        mask.fill(255); // white
        mask.rectMode(CORNER);
        mask.rect(clippingAreaOffsetX, clippingAreaOffsetY, clippingArea.getWidth(), clippingArea.getHeight());
        // reset the blend mode to normal
        mask.blendMode(BLEND);
        // draw start and end onto the clipping mask
        mask.rectMode(CENTER);
        mask.fill(0); // black
        mask.rect(start.x - objectCenter.x, start.y - objectCenter.y, tileSize, tileSize);
        mask.rect(end.x - objectCenter.x, end.y - objectCenter.y, tileSize, tileSize);
        // end working on mask
        mask.endDraw();

        // draw the transition effect using the mask

        canvas.beginDraw();
        canvas.translate(mask.width * 0.5f, mask.height * 0.5f); // set to center
        canvas.background(240, 0);
        canvas.scale(256 / 100f);
        canvas.rectMode(CENTER);
        canvas.noStroke();
        canvas.fill(0, 0, 255); // blue
        canvas.rect(0, 0, size, size);  // TODO: replace with actual sprite
        canvas.endDraw();
        canvas.mask(mask); // do masking

        // draw the transition onto the actual page

        graphics.pushMatrix();
        graphics.imageMode(CENTER);
        graphics.translate(objectCenter.x, objectCenter.y);
        graphics.scale(100 / 256f);
        graphics.image(canvas, 0, 0); //player
        graphics.popMatrix();
    }
}
