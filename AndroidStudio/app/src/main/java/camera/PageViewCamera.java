package camera;

import static processing.core.PConstants.CORNERS;

import game.AppLogic;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class PageViewCamera {

    static private PApplet p;

    static private Rectangle focusArea; // always represents the current page area
    static private float areaPadding; // amount to pad the sides of the camera around page area

    static private Rectangle cameraArea;
    static private Rectangle newCameraArea;

    static private PVector center;
    static private PVector newCenter;

    static private float scale;
    static private float newScale;

    static private float subScale = 1; // defaults to 1
    static private float newSubScale = 1; // defaults to 1

    static private final float zoomSpeed = 0.05f; // lower is faster

    static private Rectangle screenArea; // represents the region of the level that will be visible on screen

    public PageViewCamera(PApplet papplet) {
        p = papplet;

        areaPadding = 100;

        // setup temp initial values
        focusArea = new Rectangle(0, 0, 0, 0);

        cameraArea = new Rectangle(0, 0, 0, 0);
        newCameraArea = cameraArea.copy();

        center = new PVector(0, 0);
        newCenter = center.copy();

        scale = 1;
        newScale = scale;

        subScale = 1;
        newSubScale = subScale;

        screenArea = new Rectangle(0, 0, 0, 0);

    }

    public float getScale() {
        return scale;
    }

    public float getSubScale() {
        return subScale;
    }

    public PVector getCenter() {
        return center;
    }

    public PVector getTopLeft() {
        return focusArea.getTopLeft();
    }

    public PVector getBottomRight() {
        return focusArea.getBottomRight();
    }

    public boolean step(float deltaTime) {
        boolean willMove = true; // will the camera actually move (significantly) next step

        if (!cameraArea.sameDimensions(newCameraArea)) { // camera is changing this step
            // update camera sub scale
            newSubScale = calculateSubScale(newCameraArea.getWidth(), newCameraArea.getHeight());

            // update screen area rectangle
            screenArea = calculateScreenArea(cameraArea, false);
        }

        // amount to lerp all the values
        float lerpAmount = (float) (1 - Math.pow(zoomSpeed, deltaTime));

        // vertical scale
        if (subScale != newSubScale) {
            subScale = PApplet.lerp(subScale, newSubScale, lerpAmount);
        }
        // main scale
        if (scale != newScale) {
            scale = PApplet.lerp(scale, newScale, lerpAmount);
        }
        // translate
        if (center != newCenter) {
            center = PVector.lerp(center, newCenter, lerpAmount);
        }
        // move camera focus region
        if (!cameraArea.sameDimensions(newCameraArea)) {
            float topLeftX = PApplet.lerp(cameraArea.getTopLeft().x, newCameraArea.getTopLeft().x, lerpAmount);
            float topLeftY = PApplet.lerp(cameraArea.getTopLeft().y, newCameraArea.getTopLeft().y, lerpAmount);
            float bottomRightX = PApplet.lerp(cameraArea.getBottomRight().x, newCameraArea.getBottomRight().x, lerpAmount);
            float bottomRightY = PApplet.lerp(cameraArea.getBottomRight().y, newCameraArea.getBottomRight().y, lerpAmount);
            cameraArea.setCorners(topLeftX, topLeftY, bottomRightX, bottomRightY);
        }

        if (Math.abs(center.x - newCenter.x) < 3 && Math.abs(center.y - newCenter.y) < 3) { // 0.5 //1
            // this only returns false when the numbers are very similar to each other
            // which means that the camera isn't moving
            willMove = false;
        }

        return willMove;
    }

    private static float calculateSubScale(float cameraWidth, float cameraHeight) {
        // calculate newCamera area values
        float cameraHeightByWidthRatio = cameraHeight / cameraWidth;

        // calculate phone screen area values
        float screenWidth = p.width;

        // calculate level drawing space values
        float levelAreaHeight = AppLogic.drawUI.getLevelAreaHeight();
        float levelHeightByWidthRatio = AppLogic.drawUI.getLevelHeightByWidthRatio();

        if (cameraHeightByWidthRatio > levelHeightByWidthRatio) {
            // newCamera area has a taller aspect ratio than the phone screen
            // do 'subScale' field in addition to the 'scale' field
            return levelAreaHeight / (screenWidth / cameraWidth) / cameraHeight;
        } else {
            // newCamera area has shorter aspect ratio than the phone screen
            // leave camera scale to the 'scale' field
            return 1;
        }
    }

    /**
     * Draw a visualisation of the cameras logic, useful for laying out levels and debugging the camera.
     *
     * @param scale the current camera scale, used for line thickness
     */
    public void draw(float scale) {
        int strokeWeight = Math.max(1, (int) (scale * 0.5f));

        // draw page area (region directly around the active pages)
//        p.noFill();
//        p.stroke(100, 170); // grey
//        p.strokeWeight(strokeWeight);
//        p.rectMode(CORNERS);
//        p.rect(focusArea.getTopLeft().x, focusArea.getTopLeft().y, focusArea.getBottomRight().x, focusArea.getBottomRight().y);

        // draw screen area (region that will be rendered in game)
        if (screenArea != null) {
            p.noFill();
            p.stroke(100, 170); // grey
            p.strokeWeight(strokeWeight);
            p.rectMode(CORNERS);
            p.rect(screenArea.getTopLeft().x, screenArea.getTopLeft().y, screenArea.getBottomRight().x, screenArea.getBottomRight().y);
        }

        // draw camera area (padded region around the active pages)
        p.noFill();
        p.stroke(100, 170); // grey
        p.strokeWeight(strokeWeight);
        p.rectMode(CORNERS);
        p.rect(cameraArea.getTopLeft().x, cameraArea.getTopLeft().y, cameraArea.getBottomRight().x, cameraArea.getBottomRight().y);

    }

    /**
     * Passes a region of the page view to the camera for it to focus on.
     * Used when focusing on the level during gameplay.
     *
     * @param minX leftmost edge
     * @param minY topmost edge
     * @param maxX rightmost edge
     * @param maxY bottommost edge
     */
    public void updateGameplay(float minX, float minY, float maxX, float maxY) {
        // update page area boundary
        focusArea.setCorners(minX, minY, maxX, maxY);

        // update newCamera area
        updateNewCamera();

        // update newScale
        newScale = newCameraArea.getWidth();

        // update newCenter and add offset
        newCenter = newCameraArea.getRectangleCenter();
        // calculate what sub scale will be, needed for offset calculation
        float futureSubScale = calculateSubScale(newCameraArea.getWidth(), newCameraArea.getHeight());
        // calculate offset
        float offset = AppLogic.drawUI.getLevelYOffset() / ((float) p.width / newScale) / futureSubScale;
        // add offset to newCenter
        newCenter.y -= offset;
    }

    /**
     * Passes a region of the page view to the camera for it to focus on.
     * Used when focusing on a menu, so no UI offset.
     *
     * @param minX leftmost edge
     * @param minY topmost edge
     * @param maxX rightmost edge
     * @param maxY bottommost edge
     */
    public void updateMenu(float minX, float minY, float maxX, float maxY) {
        // update page area boundary
        focusArea.setCorners(minX, minY, maxX, maxY);

        // update newCamera area
        updateNewCamera();

        // update newScale
        newScale = newCameraArea.getWidth();

        // update newCenter and add offset
        newCenter = newCameraArea.getRectangleCenter();
    }

    public void initCamera(float minX, float minY, float maxX, float maxY) {
        // update page area boundary
        focusArea.setCorners(minX, minY, maxX, maxY);

        // set camera area
        float topLeftX = focusArea.getTopLeft().x - areaPadding;
        float topLeftY = focusArea.getTopLeft().y - areaPadding;
        float bottomRightX = focusArea.getBottomRight().x + areaPadding;
        float bottomRightY = focusArea.getBottomRight().y + areaPadding;
        cameraArea.setCorners(topLeftX, topLeftY, bottomRightX, bottomRightY);
        newCameraArea = cameraArea.copy();

        // set center, no offset, assumes focusing on a menu
        newCenter = newCameraArea.getRectangleCenter();
        newCenter = center.copy();

        // set scale
        scale = newCameraArea.getWidth();
        newScale = scale;
    }

    private static void updateNewCamera() {
        // add padding
        float topLeftX = focusArea.getTopLeft().x - areaPadding;
        float topLeftY = focusArea.getTopLeft().y - areaPadding;
        float bottomRightX = focusArea.getBottomRight().x + areaPadding;
        float bottomRightY = focusArea.getBottomRight().y + areaPadding;
        // update newCameraArea
        newCameraArea.setCorners(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }

    public Rectangle getCameraArea() {
        return cameraArea;
    }

    public float getSideAreaPadding() {
        return areaPadding;
    }

    public void rebuildCameraArea() {
        cameraArea = newCameraArea.copy(); // Rectangle(0, 0, 0, 0);
    }

    static public PVector screenToLevel(float screenX, float screenY) {
        PVector output = new PVector();
        output.x = ((screenX - p.width * 0.5f) / ((float) p.width / scale) / subScale) + center.x;
        output.y = ((screenY - p.height * 0.5f) / ((float) p.width / scale) / subScale) + center.y;
        return output;
    }

    /**
     * Calculates the area of the level that will be rendered on screen given a camera focus region
     *
     * @param area the focus region we want to calculate the on screen render region for
     * @param menu is this region a menu
     *
     * @return screen visible area
     */
    public Rectangle calculateScreenArea(Rectangle area, boolean menu) {
        // get the screen rectangle given an area of the level that will be focussed on

        if (area != null) {
            float cameraScale = area.getWidth();
            float cameraSubScale = calculateSubScale(area.getWidth(), area.getHeight());

            float width = area.getWidth() / cameraSubScale;
            float widthDiff = (width - area.getWidth()) * 0.5f;

            // only apply this when focusing on level, not menu
            float offset = 0;
            if (!menu) {
                offset = AppLogic.drawUI.getLevelYOffset() / ((float) p.width / cameraScale) / cameraSubScale;
            }

//            float height = width * AppLogic.drawUI.getLevelHeightByWidthRatio(); // what will be in the inside UI bounds
            float height = width * AppLogic.drawUI.getScreenHeightByWidthRatio(); // what will be in the full screen
            float heightDiff = (height - area.getHeight()) * 0.5f;

            return new Rectangle(area.getX() - widthDiff, area.getY() - heightDiff - offset, width, height);

        }

        return null;
    }

    public static Rectangle getScreenArea() {
        return screenArea;
    }
}