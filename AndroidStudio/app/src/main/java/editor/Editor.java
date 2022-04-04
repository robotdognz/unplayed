package editor;

import java.util.ArrayList;
import java.util.HashSet;

import org.jbox2d.common.Vec2;

import camera.Camera;
import controllers.CameraControl;
import controllers.EditorControl;
import controllers.PlayerControl;
import editor.tools.TileTool;
import editor.uibottom.EditorBottom;
import editor.uiside.EditorSide;
import editor.uitop.EditorTop;
import game.AppLogic;
import game.PageView;
import game.Quadtree;
import game.player.Player;
import handlers.BackgroundHandler;
import handlers.EventHandler;
import handlers.ImageHandler;
import handlers.TileHandler;
import misc.CountdownTimer;
import misc.DoToast;
import misc.EditorJSON;
import objects.Background;
import objects.Event;
import objects.Image;
import objects.Page;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import processing.core.*;
import ui.Menu;

import static processing.core.PConstants.*;

public class Editor {
    private boolean nextTouchInactive = false;

    public EditorSettings settings;

    private PApplet p;
    public DoToast toast;
    PageView pageView;
    public Quadtree world;
    public Camera camera;

    // camera variables
    final public float minZoom = 3;
    final public float maxZoom = 100;
    // page view camera backup
    private float pvScale;
    private float pvSubScale;
    private PVector pvCenter;
    // level view camera backup
    private float lvScale;
    private float lvSubScale;
    private PVector lvCenter;

    // controller
//	public Controller controller; // holds the current controller
    public boolean controllerActive = true; // is the current controller active
    public Rectangle point = null; // holds the current selection point in the game world
    public boolean eventVis; // are events visible

    public boolean viewVis; // are the views being drawn in level view

    // editor settings
    public boolean snap = true; // things placed in the level will snap to grid
    public Tool currentTool;
    public editorMode eMode;
    public static boolean showPageView = false; // are we seeing the page view

    // current object to put into level
    public TileHandler currentTile = null;
    public ImageHandler currentImage = null;
    public BackgroundHandler currentBackground = null;
    public EventHandler currentEvent = null;
    public View currentView = null;
    public Page currentPage = null;

    // selected object
    public Rectangle selected;

    // toolbars
    public Toolbar editorTop;
    public Toolbar editorBottom;
    public Toolbar editorSide;

    // saver/loader class
    public EditorJSON eJSON;

    // objects to be rendered in level build view
    public HashSet<Rectangle> screenObjects;

    // frame count and debug visualization
    private int textSize;
    private int frameDelay;
    private float frame;

    private CountdownTimer tapTimer; // prevent taps for a time after each tap

    public Editor(PApplet p, Camera camera, DoToast toast) {
        this.p = p;
        this.toast = toast;
        this.pageView = AppLogic.game.getPageView();
        this.world = AppLogic.game.getWorld();
        this.camera = camera;
//		this.controller = new CameraControl(p, this);
        this.editorTop = new EditorTop(p, this);
        AppLogic.controller = new CameraControl(p, this);
        this.editorBottom = new EditorBottom(p, this, AppLogic.texture);
        this.editorSide = new EditorSide(p, this);
        this.eJSON = new EditorJSON(p, AppLogic.texture, toast);

        this.currentTool = new TileTool(this);
        this.eMode = editorMode.ADD;
        this.eventVis = true;
        this.viewVis = true;

        // Initialize camera backup fields
        lvScale = Camera.getScale();
        lvSubScale = Camera.getSubScale();
        lvCenter = new PVector(Camera.getCenter().x, Camera.getCenter().y);
        pvScale = Camera.getScale();
        pvSubScale = Camera.getSubScale();
        pvCenter = new PVector(Camera.getCenter().x, Camera.getCenter().y);

        // debug display
        frameDelay = 60; // 100
        textSize = (int) (p.width / 28.8); // 50

        // Initialize screen objects set
        screenObjects = new HashSet<Rectangle>();

        tapTimer = new CountdownTimer(0.1f);
        settings = new EditorSettings();
    }

    public void step(ArrayList<PVector> touches, float deltaTime) {
        tapTimer.deltaStep(deltaTime);

        editorTop.step();
        editorBottom.step();
        editorSide.step();

        // step the controller if there are no widget menus open and touch has been
        // re-enabled
        if (controllerActive && !nextTouchInactive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
                && !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
            AppLogic.controller.step(touches); // draw event for controls
        }

        frameCounter();

        if (showPageView) {
            point = null;
        } else {
            // get objects to draw in level build view
            screenObjects.clear();
            world.retrieve(screenObjects, AppLogic.game.screenSpace);
        }

        if (!(AppLogic.controller instanceof EditorControl)) {
            point = null;
        }

        // this is jank as hell
        if (AppLogic.game.player != null) {
            if (EditorSettings.playerLogic()) {
                AppLogic.game.player.showChecking = true;
            } else {
                AppLogic.game.player.showChecking = false;
            }
        }

    }

    public void draw(float deltaTime, PVector touch, Menu menu) {

        if (!showPageView) {
            // draw the level build view
            drawLevel();
        } else {
            // draw on screen tool information
            if (!Camera.getGame()) {
                float currentScale = AppLogic.convert.getScale();
                // start working at game scale
                p.pushMatrix();
                p.translate(p.width / 2, p.height / 2);
                p.scale((float) p.width / (float) Camera.getScale());
                p.scale(Camera.getSubScale());
                p.translate(-Camera.getCenter().x, -Camera.getCenter().y);

                // draw selection box around selected object
                if (selected != null && !(AppLogic.controller instanceof PlayerControl)) {
                    if (selected instanceof Page || selected instanceof Background) {
                        selected.drawSelected(p.g, currentScale);
                    }
                }
                // draw tool effects
                if (currentTool != null) {
                    currentTool.draw();
                }
                // start working at screen scale
                p.popMatrix();
            }
        }

        // draw toolbars
        editorTop.draw(touch, menu, deltaTime);
        editorBottom.draw(touch, menu, deltaTime);
        editorSide.draw(touch, menu, deltaTime);

        // draw frame counter and other readouts
        if (EditorSettings.debugOutput()) {
            p.fill(80);
            p.textSize(textSize);
            p.textAlign(CENTER, CENTER);
            if (AppLogic.game.player != null) {
                Vec2 vel = Player.dynamicBody.getLinearVelocity();
                float aVel = Player.dynamicBody.getAngularVelocity();
                p.text("Velocity x: " + PApplet.nf(Math.abs(vel.x), 1, 2) + " y: " + PApplet.nf(Math.abs(vel.y), 1, 2)
                                + " a: " + PApplet.nf(Math.abs(aVel), 1, 2), p.width / 2,
                        p.height - editorBottom.getHeight() - textSize * 8);
                float angle = PApplet.degrees(Player.dynamicBody.getAngle());
                p.text("Angle: " + PApplet.nf(angle, 1, 4), p.width / 2,
                        p.height - editorBottom.getHeight() - textSize * 7);
                p.text("ground: " + AppLogic.game.player.groundContacts + " left wall: "
                                + AppLogic.game.player.leftWallContacts + " right wall: "
                                + AppLogic.game.player.rightWallContacts, p.width / 2,
                        p.height - editorBottom.getHeight() - textSize * 6);
            }
            if (AppLogic.game.placed != null) {
                p.text("Placed: " + AppLogic.game.placed.size(), p.width / 2,
                        p.height - editorBottom.getHeight() - textSize * 5);
            }
            if (AppLogic.game.removed != null) {
                p.text("Removed: " + AppLogic.game.removed.size(), p.width / 2,
                        p.height - editorBottom.getHeight() - textSize * 4);
            }
            p.text(PApplet.nf(AppLogic.convert.getScale(), 1, 2), p.width / 2,
                    p.height - editorBottom.getHeight() - textSize * 3);
            p.text("FPS: " + PApplet.nf(this.frame, 1, 2), p.width / 2,
                    p.height - editorBottom.getHeight() - textSize * 2);
            p.text("DT: " + PApplet.nf(deltaTime, 1, 4), p.width / 2, p.height - editorBottom.getHeight() - textSize);

            // draw debug messages
            DebugOutput.drawMessages(p, editorTop.getHeight() + textSize, textSize);
            DebugOutput.step(deltaTime);
        }
    }

    private void drawLevel() {
        // start working at game scale
        p.pushMatrix();
        p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen

        // camera
        p.scale((float) p.width / (float) Camera.getScale()); // width/screen fits the level scale to the screen
        p.scale(Camera.getSubScale()); // apply offset for tall screen spaces
        p.translate(-Camera.getCenter().x, -Camera.getCenter().y); // moves the view around the level

        float currentScale = AppLogic.convert.getScale();

        p.background(240);

        // find corners of camera
        PVector currentTopLeft = AppLogic.game.screenSpace.getTopLeft();
        PVector currentBottomRight = AppLogic.game.screenSpace.getBottomRight();

        // draw player and environment
        // TODO: this system is not great, I need to change this so it only eliminates
        // things that shouldn't be drawn once

        for (Rectangle r : screenObjects) { // draw images
            if (!(r instanceof Image)) {
                continue;
            }
            if (r.getTopLeft().x > currentBottomRight.x - 1) {
                continue;
            }
            if (r.getBottomRight().x < currentTopLeft.x + 1) {
                continue;
            }
            if (r.getTopLeft().y > currentBottomRight.y - 1) {
                continue;
            }
            if (r.getBottomRight().y < currentTopLeft.y + 1) {
                continue;
            }
            ((Image) r).draw(p.g, currentScale);
        }
        for (Rectangle r : screenObjects) { // draw tiles on top of images
            if (!(r instanceof Tile)) {
                continue;
            }
            if (r.getTopLeft().x > currentBottomRight.x - 1) {
                continue;
            }
            if (r.getBottomRight().x < currentTopLeft.x + 1) {
                continue;
            }
            if (r.getTopLeft().y > currentBottomRight.y - 1) {
                continue;
            }
            if (r.getBottomRight().y < currentTopLeft.y + 1) {
                continue;
            }
            ((Tile) r).draw(p.g, currentScale);
        }

        if (AppLogic.game.player != null) { // draw the player on top of tiles and images
            AppLogic.game.player.draw(p.g, currentScale);
        }

        if (viewVis) { // draw the views behind events
            for (View view : AppLogic.game.views) {
                view.draw(p.g);
            }
        }
        for (Rectangle r : screenObjects) { // draw events on top of player, tiles, and images
            if (r instanceof Event && (eventVis || ((Event) r).visible)) {
                ((Event) r).draw(p.g, currentScale);
            }
        }

        AppLogic.game.paper.draw(p.g, AppLogic.game.screenSpace, currentScale, 1);

        // draw tool effects
        if (currentTool != null) {
            currentTool.draw();
        }

        // draw quad tree logic for testing
        if (EditorSettings.quadTreeLogic()) {
            world.draw(p, currentScale);
            p.fill(0, 0, 0, 150);
            for (Rectangle r : AppLogic.game.playerObjects) {
                p.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            }
        }

        // draw block placement selection if one exists and snapping is turned off
        if (point != null && !snap) {
            p.fill(0, 0, 0, 150);
            p.noStroke();
            p.rect(point.getX(), point.getY(), 100, 100);
            p.fill(0);
            p.textSize(30);
            p.textAlign(LEFT, CENTER);
            int xCoordinate = (int) point.getX();
            int yCoordinate = (int) point.getY();
            String s = "[" + xCoordinate + ", " + yCoordinate + "]";
            p.text(s, point.getX() + 105, point.getY() + 50);
        }

        // draw selection box around selected object
        if (selected != null && !(selected instanceof Page || selected instanceof Background)
                && !(AppLogic.controller instanceof PlayerControl)) {
            selected.drawSelected(p.g, currentScale);
        }

        p.popMatrix(); // start working at screen scale
    }

    public float getBottom() {
        // used to determine the draw position on the on screen controls
        // editorBottom.getHeight() returns the ribbon dimensions, got to increase it to include the widgets
//        return editorBottom.getHeight() * 1.4f;
        return ((EditorBottom) editorBottom).getTopOfBounds();
    }


    private void frameCounter() {
        // update frame rate average
        if (frameDelay > 30) {
            this.frame = p.frameRate;
            this.frameDelay = 0;
        } else {
            this.frameDelay++;
        }
    }

    public void touchStarted(PVector touch) {
        if (nextTouchInactive) {
            return;
        }
        if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
                && !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
            AppLogic.controller.touchStarted(touch); // Controls for touch started event
        }
    }

    public void touchEnded(PVector touch) {
        if (nextTouchInactive) {
            nextTouchInactive = false;
        }

        editorTop.touchEnded();
        editorBottom.touchEnded();
        editorSide.touchEnded();

        if (nextTouchInactive) { // don't do controller if next touch inactive
            nextTouchInactive = false;
            return;
        }
        if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
                && !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
            AppLogic.controller.touchEnded(touch); // Controls for touch moved event
        }

    }

    public void touchMoved(PVector touch, ArrayList<PVector> touches) {
        editorTop.touchMoved(touches);
        editorBottom.touchMoved(touches);
        editorSide.touchMoved(touches);

        if (nextTouchInactive) { // don't do controller if next touch inactive
            return;
        }
        if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
                && !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
            AppLogic.controller.touchMoved(touch, touches); // Controls for touch moved event
        }
    }

    public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
        if (nextTouchInactive) {
            return;
        }
        if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
                && !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
            AppLogic.controller.onPinch(touches, x, y, d); // controls for on pinch event
        }
    }

    public void onRotate(float x, float y, float angle) {
        if (nextTouchInactive) {
            return;
        }
        if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
                && !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
            AppLogic.controller.onRotate(x, y, angle); // controls for on rotate event
        }
    }

    public void onTap(float x, float y) {
        if (tapTimer.isRunning()) {
            return;
        } else {
            editorBottom.onTap(x, y);
            tapTimer.start();

            if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
                    && !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
                AppLogic.controller.onTap(x, y); // controls for on rotate event
            }
        }
    }

    public void switchView() {
        if (showPageView) {
            showPageView = false;
            // save page view camera
            pvScale = Camera.getScale();
            pvSubScale = Camera.getSubScale();
            pvCenter.x = Camera.getCenter().x;
            pvCenter.y = Camera.getCenter().y;
            // set camera to level view
            Camera.setScale(lvScale);
            Camera.setSubScale(lvSubScale);
            Camera.setCenter(lvCenter);
        } else {
            showPageView = true;
            // save level view camera
            lvScale = Camera.getScale();
            lvSubScale = Camera.getSubScale();
            lvCenter.x = Camera.getCenter().x;
            lvCenter.y = Camera.getCenter().y;
            // set camera to page view
            Camera.setScale(pvScale);
            Camera.setSubScale(pvSubScale);
            Camera.setCenter(pvCenter);

            // force re-render of pages
            pageView.recalculatePageViewObjects();
        }
    }

    public void nextTouchInactive() {
        nextTouchInactive = true;
    }

    public void resetUI() {
        editorTop.resetWidgets();
        editorSide.resetWidgets();
    }

    public enum editorMode {
        ADD, ERASE, SELECT, EXTERNAL
    }
}
