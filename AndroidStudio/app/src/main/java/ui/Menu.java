package ui;

import java.util.ArrayList;

import camera.Camera;
import camera.PageViewCamera;
import game.AppLogic;
import handlers.Handler;
import handlers.TextureCache;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PConstants.*;

import android.annotation.SuppressLint;

public abstract class Menu {
    public Menu child;

    protected PApplet p;
    protected float buttonHeight;
    protected float buttonWidth;
    protected float menuTopY;
    protected float menuCenterX;
    protected float menuWidth;
    protected float menuHeight;
    protected float buttonDistance;
    protected ArrayList<MenuObject> objects = new ArrayList<>();

    protected Handler image = null;

    // page view menu
    protected Rectangle pageMenu;
    PVector position;
    float angle = 0;
    // Page corners relative to center, used to check if the page is on screen
    PVector topLeft;
    PVector topRight;
    PVector bottomLeft;
    PVector bottomRight;

    //	private float size = 1;
//	private int shadowOffset; // the absolute amount to offset the shadow by
    private int shadow; // the relative amount to offset the shadow by

    protected boolean built = false;
    float angleOffset; // used for random angle when created

    static boolean previousTilt = false;
    static PVector previousPosition = null;

    public Menu(PApplet p) {
        this.p = p;
        buttonWidth = 400;
        buttonHeight = 100;
        buttonDistance = 100;

//		this.shadowOffset = 9;
        this.shadow = 9;
        this.angleOffset = 5;
    }

    protected void setAngle(float range) {
        if (previousTilt) {
            angle = angleOffset;
            previousTilt = !previousTilt;
        } else {
            angle = -angleOffset;
            previousTilt = !previousTilt;
        }

        angle += (float) (Math.random() * range) - (range / 2);
    }

    public void setImage(Handler handler) {
        this.image = handler;
    }

    protected void constructMenu() {
        // get's called in the child class constructor
        // create basic menu
        menuCenterX = p.width / 2;
        menuWidth = buttonDistance * 2; // buttonWidth + buttonDistance * 2;
        menuHeight = buttonDistance;
        float largestWidth = 0;
        for (MenuObject object : objects) {
            menuHeight += object.getHeight() + buttonDistance;
            if (object.getWidth() > largestWidth) {
                largestWidth = object.getWidth();
            }
        }
        menuWidth += largestWidth;

        menuTopY = p.height / 2 - menuHeight / 2;

    }

    public void buildPageMenu(PVector cameraCenter, Rectangle pageArea, PageViewCamera camera) {
        // called when there is a level on screen and we want to construct a menu off
        // screen

        // TODO: this needs work, it should build the menu on the closest side and not use the current camera position

        position = new PVector(0, 0);

        // figure out side of pageArea that cameraCenter is closest to
        float leftDiff = Math.abs(cameraCenter.x - pageArea.getTopLeft().x) + camera.getSideAreaPadding();
        float rightDiff = Math.abs(pageArea.getBottomRight().x - cameraCenter.x) + camera.getSideAreaPadding();
        float offset = 200;

        if (leftDiff < rightDiff) {
            // left
            position = new PVector(cameraCenter.x - leftDiff - (menuWidth / 2) - offset, cameraCenter.y);
        } else {
            // right
            position = new PVector(cameraCenter.x + rightDiff + (menuWidth / 2) + offset, cameraCenter.y);
        }

        // create page view menu and buttons
        setupMenuContents();
    }

    public void buildPageMenu() {
        // called when we just want to make a menu in the middle of the screen

        position = new PVector(0, 0);

        // create page view menu and buttons
        setupMenuContents();
    }

    protected void setupMenuContents() {
        // create page view menu and buttons
        pageMenu = new Rectangle(0 - menuWidth / 2, 0 - menuHeight / 2, menuWidth, menuHeight);

        float objectYPosition = pageMenu.getY();
        for (MenuObject object : objects) {
            objectYPosition += buttonDistance;
            float objectHeight = object.getHeight();

            if (object instanceof Button) {
                Button button = (Button) object;
                button.setupPageButton(pageMenu.getTopLeft().x + pageMenu.getWidth() / 2,
                        objectYPosition + objectHeight * 0.5f);
            }

            objectYPosition += objectHeight;
        }

        setAngle(0);
//		updateShadow();
        updateCorners();
        built = true;
    }

//	private void updateShadow() {
//		this.shadow = (int) (shadowOffset / size);
//	}

    public void drawInWorld(float scale) {

        p.pushMatrix();
        p.translate(position.x, position.y);

        // draw the shadow
        p.translate(shadow, shadow);
        p.fill(0, 40);
        p.noStroke();
        p.rectMode(CENTER);
        p.rotate(PApplet.radians(angle)); // rotate the page
        p.rect(0, 0, menuWidth, menuHeight);
        p.rotate(PApplet.radians(-angle)); // rotate the page
        p.translate(-shadow, -shadow);
        p.rotate(PApplet.radians(angle)); // rotate the page

        // draw white background
        p.noStroke();
        p.fill(240);
        p.rectMode(CENTER);
        p.rect(0, 0, pageMenu.getWidth(), pageMenu.getHeight());

        // draw the buttons
        p.imageMode(CENTER);
        float objectYPosition = -pageMenu.getHeight() / 2;
        for (MenuObject object : objects) {
            objectYPosition += buttonDistance;
            float objectHeight = object.getHeight();
            object.drawOnPage(p, 0, objectYPosition + objectHeight * 0.5f);

            objectYPosition += objectHeight;
        }

        // draw grid paper
        int gridSize = 400;
        float startX = -menuWidth / 2;
        // find y start position;
        float startY = -menuHeight / 2;
        // find x end position
        float endX = menuWidth / 2;
        // find y end position
        float endY = menuHeight / 2;
        float xTileStart = 0; // where to start horizontal tiling in texture units
        float yTileStart = 0; // where to start vertical tiling in texture units
        float xTileEnd = menuWidth / gridSize; // where to end horizontal tiling in texture units
        float yTileEnd = menuHeight / gridSize; // where to end vertical tiling in texture units
        // texture
        p.noStroke();
        p.textureMode(NORMAL);
        p.beginShape();
        p.textureWrap(REPEAT);
        p.texture(TextureCache.getGrid(scale));
        p.vertex(startX, startY, xTileStart, yTileStart); // top left
        p.vertex(endX, startY, xTileEnd, yTileStart); // top right
        p.vertex(endX, endY, xTileEnd, yTileEnd); // bottom right
        p.vertex(startX, endY, xTileStart, yTileEnd); // bottom left
        p.endShape();

        p.popMatrix();

        if (child != null && child.isBuilt()) {
            child.drawInWorld(scale);
        }
    }

    public void drawOnTop() {
        // used only in editor
        p.stroke(0);
        p.strokeWeight(6);
        p.fill(240);
        p.rectMode(CORNER);
        p.rect(menuCenterX - menuWidth / 2, menuTopY, menuWidth, menuHeight);
        // draw the buttons
        p.noStroke();
        for (int i = 0; i < objects.size(); i++) {
            float y = menuTopY + buttonDistance + (buttonHeight + buttonDistance) * i + buttonHeight / 2;
            objects.get(i).draw(p, y);
        }

        p.pushMatrix();
        p.translate(menuCenterX, menuTopY + menuHeight / 2);

        // draw grid paper
        int gridSize = 400;
        float startX = -menuWidth / 2;
        // find y start position;
        float startY = -menuHeight / 2;
        // find x end position
        float endX = menuWidth / 2;
        // find y end position
        float endY = menuHeight / 2;
        float xTileStart = 0; // where to start horizontal tiling in texture units
        float yTileStart = 0; // where to start vertical tiling in texture units
        float xTileEnd = menuWidth / gridSize; // where to end horizontal tiling in texture units
        float yTileEnd = menuHeight / gridSize; // where to end vertical tiling in texture units
        // texture
        p.noStroke();
        p.textureMode(NORMAL);
        p.beginShape();
        p.textureWrap(REPEAT);
        p.texture(TextureCache.getGrid(4));
        p.vertex(startX, startY, xTileStart, yTileStart); // top left
        p.vertex(endX, startY, xTileEnd, yTileStart); // top right
        p.vertex(endX, endY, xTileEnd, yTileEnd); // bottom right
        p.vertex(startX, endY, xTileStart, yTileEnd); // bottom left
        p.endShape();

        p.popMatrix();
    }

    public void drawCorners(float scale) {
        int cornerBoxSize = (int) scale * 2;
        int strokeWeight = Math.max(1, (int) (scale * 0.5f));
        p.strokeWeight(strokeWeight);
        // draw page corners
        p.rectMode(CENTER);
        p.stroke(100, 170); // grey
        p.noFill();
        p.rect(topLeft.x, topLeft.y, cornerBoxSize, cornerBoxSize);
        p.rect(topRight.x, topRight.y, cornerBoxSize, cornerBoxSize);
        p.rect(bottomLeft.x, bottomLeft.y, cornerBoxSize, cornerBoxSize);
        p.rect(bottomRight.x, bottomRight.y, cornerBoxSize, cornerBoxSize);
    }

    public void hover(PVector lastTouch) {
        if (Camera.getGame()) {
            // interacting with in world menu

            PVector point = PageViewCamera.screenToLevel(lastTouch.x, lastTouch.y);
            point.x -= position.x;
            point.y -= position.y;
            point.rotate(PApplet.radians(-angle));

            for (MenuObject object : objects) {
                if (!(object instanceof Button)) {
                    continue;
                }
                ((Button) object).hoverPage(point); // levelTouch
            }
        } else {
            // interacting with menu overlay
            for (MenuObject object : objects) {
                if (!(object instanceof Button)) {
                    continue;
                }
                ((Button) object).hover(lastTouch);
            }
        }
    }

    public void click() {
        // this gets overwritten by child classes
    }

    public void activate() {

    }

    // --------------update the corner PVectors---------------
    protected void updateCorners() {
        if (topLeft == null) {
            // Initialize
            topLeft = new PVector();
            topRight = new PVector();
            bottomLeft = new PVector();
            bottomRight = new PVector();
        }
        // set values
        topLeft.x = 0 - (pageMenu.getWidth() / 2);
        topLeft.y = 0 - (pageMenu.getHeight() / 2);
        topRight.x = 0 + (pageMenu.getWidth() / 2);
        topRight.y = 0 - (pageMenu.getHeight() / 2);
        bottomLeft.x = 0 - (pageMenu.getWidth() / 2);
        bottomLeft.y = 0 + (pageMenu.getHeight() / 2);
        bottomRight.x = 0 + (pageMenu.getWidth() / 2);
        bottomRight.y = 0 + (pageMenu.getHeight() / 2);
        // rotate
        topLeft.rotate(PApplet.radians(angle));
        topRight.rotate(PApplet.radians(angle));
        bottomLeft.rotate(PApplet.radians(angle));
        bottomRight.rotate(PApplet.radians(angle));
        // translate
        topLeft.x += position.x;
        topLeft.y += position.y;
        topRight.x += position.x;
        topRight.y += position.y;
        bottomLeft.x += position.x;
        bottomLeft.y += position.y;
        bottomRight.x += position.x;
        bottomRight.y += position.y;
    }

    // get the bounding box edges for the page
    public float getLeftmostPoint() {
        return Math.min(Math.min(topLeft.x, topRight.x), Math.min(bottomLeft.x, bottomRight.x));
    }

    public float getRightmostPoint() {
        return Math.max(Math.max(topLeft.x, topRight.x), Math.max(bottomLeft.x, bottomRight.x));
    }

    public float getTopmostPoint() {
        return Math.min(Math.min(topLeft.y, topRight.y), Math.min(bottomLeft.y, bottomRight.y));
    }

    public float getBottommostPoint() {
        return Math.max(Math.max(topLeft.y, topRight.y), Math.max(bottomLeft.y, bottomRight.y));
    }

    public Rectangle getArea() {
        float x = getLeftmostPoint();
        float y = getTopmostPoint();
        float width = getRightmostPoint() - x;
        float height = getBottommostPoint() - y;
        return new Rectangle(x, y, width, height);
    }

    public PVector getPosition() {
        return position;
    }

    // ----------is this page off camera------------

    public boolean leftOf(float x) {
        if (topLeft.x > x) {
            return false;
        }
        if (topRight.x > x) {
            return false;
        }
        if (bottomLeft.x > x) {
            return false;
        }
        if (bottomRight.x > x) {
            return false;
        }
        return true;
    }

    public boolean rightOf(float x) {
        if (topLeft.x < x) {
            return false;
        }
        if (topRight.x < x) {
            return false;
        }
        if (bottomLeft.x < x) {
            return false;
        }
        if (bottomRight.x < x) {
            return false;
        }
        return true;
    }

    public boolean above(float y) {
        if (topLeft.y > y) {
            return false;
        }
        if (topRight.y > y) {
            return false;
        }
        if (bottomLeft.y > y) {
            return false;
        }
        if (bottomRight.y > y) {
            return false;
        }
        return true;
    }

    public boolean below(float y) {
        if (topLeft.y < y) {
            return false;
        }
        if (topRight.y < y) {
            return false;
        }
        if (bottomLeft.y < y) {
            return false;
        }
        if (bottomRight.y < y) {
            return false;
        }
        return true;
    }

    public boolean isBuilt() {
        return built;
    }

    public void skipLoadingScreen() {
    }
}
