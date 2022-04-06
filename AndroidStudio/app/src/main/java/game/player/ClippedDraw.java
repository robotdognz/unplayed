package game.player;

import static processing.core.PConstants.*;

import org.jbox2d.common.Vec2;

import game.AppLogic;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class ClippedDraw {
    // player drawing algorithm
    private static PGraphics player;
    private static PGraphics playerMask;

    private static PGraphics transition;
    private static PGraphics transitionMask;

    // TODO: might be better to just combine these PGraphics objects back together

    public ClippedDraw(PApplet p) {
        // create player drawer
        int playerRez = 256;
        player = p.createGraphics(playerRez, playerRez, P2D);
        playerMask = p.createGraphics(playerRez, playerRez, P2D);

//        int transitionRez = 128;
        transition = p.createGraphics(playerRez, playerRez, P2D);
        transitionMask = p.createGraphics(playerRez, playerRez, P2D);
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
    public static void drawPlayerSimple(PGraphics graphics, Rectangle clippingArea, float scale) {
        Player playerCube = AppLogic.game.player;
        if (playerCube == null) {
            return;
        }
        float objectAngle = playerCube.getDrawingAngle();
        Vec2 objectCenter = playerCube.getCenter();

        // draw the mask at the player position and add masking

        playerMask.beginDraw();
        playerMask.background(0); // black
        playerMask.rectMode(CORNER);
        playerMask.translate(playerMask.width * 0.5f, playerMask.height * 0.5f); // set to center

        float xDiff = clippingArea.getX() - objectCenter.x;
        float yDiff = clippingArea.getY() - objectCenter.y;

        playerMask.noStroke();
        playerMask.rotate(objectAngle);
        playerMask.scale(256 / 100f);
        playerMask.fill(255); // white
        playerMask.rect(xDiff, yDiff, clippingArea.getWidth(), clippingArea.getHeight());
        playerMask.endDraw();

        // draw the player

        player.beginDraw();
        player.translate(playerMask.width * 0.5f, playerMask.height * 0.5f); // set to center
        player.background(240, 0);
        player.scale(256 / 100f);
        playerCube.drawNoTransform(player, scale);
        player.endDraw();

        player.mask(playerMask);

        graphics.pushMatrix();
        graphics.imageMode(CENTER);
        graphics.translate(objectCenter.x, objectCenter.y);
        graphics.rotate(-objectAngle);
        graphics.scale(100 / 256f);
        graphics.image(player, 0, 0);
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
        playerMask.beginDraw();
        playerMask.blendMode(BLEND);
        playerMask.background(0); // white

        // translate to middle of mask PImage
        playerMask.translate(playerMask.width * 0.5f, playerMask.height * 0.5f); // set to center

        // calculate the amount to offset the clipping area by so it correctly overlaps the mask
        float xDiff = clippingArea.getX() - objectCenter.x;
        float yDiff = clippingArea.getY() - objectCenter.y;

        // draw the clipping area rectangle over the mask in black
        playerMask.noStroke();
        playerMask.rotate(objectAngle);
        playerMask.scale(256 / 100f);
        playerMask.fill(255); // black
        playerMask.rectMode(CORNER);
        playerMask.rect(xDiff, yDiff, clippingArea.getWidth(), clippingArea.getHeight());

        // draw the sprite to be masked onto the mask PImage, tinted to black
        // this is used to determine what area of the mask will be used to draw the final sprite
        playerMask.tint(0);
        playerMask.rotate(-objectAngle);
        playerCube.drawNoTransform(playerMask, scale);
        playerMask.noTint();

        // draw the clipping area rectangle over the mask again but in white using EXCLUSION blend mode
        playerMask.blendMode(EXCLUSION);
        playerMask.fill(255); // white
        playerMask.rectMode(CORNER);
        playerMask.rotate(objectAngle);
        playerMask.rect(xDiff, yDiff, clippingArea.getWidth(), clippingArea.getHeight());

        // reset the blend mode to normal, invert the mask PImage, and end drawing
        playerMask.blendMode(BLEND);
        playerMask.endDraw();

        // draw the player

        player.beginDraw();
        player.translate(playerMask.width * 0.5f, playerMask.height * 0.5f); // set to center
        player.background(240, 0);
        player.scale(256 / 100f);
        playerCube.drawNoTransform(player, scale);
        player.endDraw();

        player.mask(playerMask);

        graphics.pushMatrix();
        graphics.imageMode(CENTER);
        graphics.translate(objectCenter.x, objectCenter.y);
        graphics.rotate(-objectAngle);
        graphics.scale(100 / 256f);
        graphics.image(player, 0, 0);
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
        float objectAngle = 0;
        Vec2 objectCenter = AppLogic.game.playerTransition.getCenter();

        // draw the mask ot the transition effect position and add masking

        // set the mask PImage background to white
        transitionMask.beginDraw();
        transitionMask.blendMode(BLEND);
        transitionMask.background(0); // white

        // translate to middle of mask PImage
        transitionMask.translate(transitionMask.width * 0.5f, transitionMask.height * 0.5f); // set to center

        // calculate the amount to offset the clipping area by so it correctly overlaps the mask
        float xDiff = clippingArea.getX() - objectCenter.x;
        float yDiff = clippingArea.getY() - objectCenter.y;

        // draw the clipping area rectangle over the mask in black
        transitionMask.noStroke();
        transitionMask.rotate(objectAngle);
        transitionMask.scale(256 / 100f);
        transitionMask.fill(255); // black
        transitionMask.rectMode(CORNER);
        transitionMask.rect(xDiff, yDiff, clippingArea.getWidth(), clippingArea.getHeight());

        // draw the sprite to be masked onto the mask PImage, tinted to black
        // this is used to determine what area of the mask will be used to draw the final sprite
        transitionMask.fill(0);
        transitionMask.rectMode(CENTER);
        transitionMask.rect(0, 0, 50, 50); // TODO: replace with actual sprite

        // draw the clipping area rectangle over the mask again but in white using EXCLUSION blend mode
        transitionMask.blendMode(EXCLUSION);
        transitionMask.fill(255); // white
        transitionMask.rectMode(CORNER);
        transitionMask.rect(xDiff, yDiff, clippingArea.getWidth(), clippingArea.getHeight());

        // reset the blend mode to normal, invert the mask PImage, and end drawing
        transitionMask.blendMode(BLEND);
        transitionMask.endDraw();

        // draw the transition effect

        transition.beginDraw();
        transition.translate(transitionMask.width * 0.5f, transitionMask.height * 0.5f); // set to center
        transition.background(240, 0);
        transition.scale(256 / 100f);
        transition.rectMode(CENTER);
        transition.noStroke();
        transition.fill(0, 0, 255); // blue
        transition.rect(0, 0, 50, 50);
        transition.endDraw();

        transition.mask(transitionMask);

        graphics.pushMatrix();
        graphics.imageMode(CENTER);
        graphics.translate(objectCenter.x, objectCenter.y);
        graphics.rotate(-objectAngle);
        graphics.scale(100 / 256f);
        graphics.image(transition, 0, 0); //player
        graphics.popMatrix();
    }
}
