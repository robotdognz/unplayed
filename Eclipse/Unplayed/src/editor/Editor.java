package editor;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import camera.Camera;
import controllers.CameraControl;
import controllers.Controller;
import controllers.EditorControl;
import controllers.PlayerControl;
import editor.tools.TileTool;
import editor.uibottom.EditorBottom;
import editor.uiside.EditorSide;
import editor.uitop.EditorTop;
import game.Game;
import game.PageView;
import game.Quadtree;
import handlers.EventHandler;
import handlers.ImageHandler;
import handlers.TextureCache;
import handlers.TileHandler;
import misc.Converter;
import misc.DoToast;
import misc.EditorJSON;
import misc.FileChooser;
import objects.Event;
import objects.Image;
import objects.Page;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.CameraChange;
import processing.core.*;
import ui.Menu;

import static processing.core.PConstants.*;

public class Editor {
	public boolean nextTouchInactive = false;

	private PApplet p;
	public TextureCache texture;
	public Converter convert;
	public DoToast toast;
	public FileChooser files;
	public Game game;
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
	public Controller controller; // holds the current controller
	public boolean controllerActive = true; // is the current controller active
	public Rectangle point = null; // holds the current selection point in the game world
	public boolean eventVis; // are events visible
	public boolean quadVis; // is the quad tree being draw
	public boolean viewVis; // are the views being drawn in level view

	// editor settings
	public boolean snap = true; // things placed in the level will snap to grid
	public Tool currentTool;
	public editorMode eMode;
	public imagePlane eImagePlane;
	public boolean showPageView = false; // are we seeing the page view

	// current object to put into level
	public TileHandler currentTile = null;
	public ImageHandler currentImage = null;
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

	// frame count and debug visualization
	private int textSize;
	public boolean debugVis;
	private int frameDelay;
	private float frame;
	public boolean tileSearch; // display the logic for searching for slots

	public Editor(PApplet p, FileChooser files, TextureCache texture, Game game, Camera camera, Converter convert,
			DoToast toast) {
		this.p = p;
		this.texture = texture;
		this.convert = convert;
		this.toast = toast;
		this.files = files;
		this.game = game;
		this.pageView = game.getPageView();
		this.world = game.getWorld();
		this.camera = camera;
		this.controller = new CameraControl(p, this);
		this.editorTop = new EditorTop(p, this);
		this.editorBottom = new EditorBottom(p, this, texture);
		this.editorSide = new EditorSide(p, this);
		this.eJSON = new EditorJSON(p, texture, toast);

		this.currentTool = new TileTool(this);
		this.eMode = editorMode.ADD;
		this.eImagePlane = imagePlane.LEVEL;
		this.eventVis = true;
		this.quadVis = false;
		this.viewVis = true;

		// Initialize camera backup fields
		lvScale = camera.getScale();
		lvSubScale = camera.getSubScale();
		lvCenter = new PVector(camera.getCenter().x, camera.getCenter().y);
		pvScale = camera.getScale();
		pvSubScale = camera.getSubScale();
		pvCenter = new PVector(camera.getCenter().x, camera.getCenter().y);

		// debug display
		debugVis = false;
		frameDelay = 60; // 100
		textSize = (int) (p.width / 28.8); // 50
		tileSearch = true; // false
	}

	public void step(ArrayList<PVector> touches) {
		editorTop.step();
		editorBottom.step();
		editorSide.step();

		// step the controller if there are no widget menus open and touch has been
		// re-enabled
		if (controllerActive && !nextTouchInactive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
				&& !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
			controller.step(touches); // draw event for controls
		}

		frameCounter();

		if (showPageView) {
			point = null;
		}

		if (!(controller instanceof EditorControl)) {
			point = null;
		}

		// this is jank as hell
		if (game.player != null) {
			if (tileSearch) {
				game.player.showChecking = true;
			} else {
				game.player.showChecking = false;
			}
		}

	}

	// a bunch of this probably needs to be moved to step, for logical consistency
	// only drawing should be in draw
	public void draw(float deltaTime, PVector touch, Menu menu) {
		// draw the level
		if (!showPageView) {
			drawLevel();
		} else {
			if (!camera.getGame()) {
				// start working at game scale
				p.pushMatrix();
				p.translate(p.width / 2, p.height / 2);
				p.scale((float) p.width / (float) camera.getScale());
				p.scale(camera.getSubScale());
				p.translate(-camera.getCenter().x, -camera.getCenter().y);

				// draw selection box around selected object
				if (selected != null && !(controller instanceof PlayerControl)) {
					if (selected instanceof Page) {
						selected.drawSelected(p.g);
					} else if (selected instanceof CameraChange) {
						((CameraChange) selected).drawCameraArea(p.g);
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
		if (debugVis) {
			p.fill(80);
			p.textSize(textSize);
			p.textAlign(CENTER, CENTER);
			if (game.player != null) {
				Vec2 vel = game.player.dynamicBody.getLinearVelocity();
				p.text("Velocity x:" + PApplet.nf(Math.abs(vel.x), 1, 2) + " y:" + PApplet.nf(Math.abs(vel.y), 1, 2),
						p.width / 2, p.height - editorBottom.getHeight() - textSize * 9);
				float angle = PApplet.degrees(game.player.dynamicBody.getAngle());
				p.text("Angle:" + PApplet.nf(angle, 1, 2), p.width / 2,
						p.height - editorBottom.getHeight() - textSize * 8);
				p.text("ground: " + game.player.groundContacts + " wall: " + game.player.wallContacts, p.width / 2,
						p.height - editorBottom.getHeight() - textSize * 7);
				p.text("Timer: " + game.player.groundTimer.isRunning(), p.width / 2,
						p.height - editorBottom.getHeight() - textSize * 6);
			}
			if (game.placed != null) {
				p.text("Placed: " + game.placed.size(), p.width / 2,
						p.height - editorBottom.getHeight() - textSize * 5);
			}
			if (game.removed != null) {
				p.text("Removed: " + game.removed.size(), p.width / 2,
						p.height - editorBottom.getHeight() - textSize * 4);
			}
			p.text(PApplet.nf(convert.getScale(), 1, 2), p.width / 2,
					p.height - editorBottom.getHeight() - textSize * 3);
//			p.text(game.playerObjects.size() + " : " + game.screenObjects.size(), p.width / 2,
//					p.height - editorBottom.getHeight() - textSize * 3);
			p.text("FPS: " + PApplet.nf(this.frame, 1, 2), p.width / 2,
					p.height - editorBottom.getHeight() - textSize * 2);
			p.text("DT: " + PApplet.nf(deltaTime, 1, 4), p.width / 2, p.height - editorBottom.getHeight() - textSize);
		}
	}

	private void drawLevel() {
		// start working at game scale
		p.pushMatrix();
		p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen

		// camera
		p.scale((float) p.width / (float) camera.getScale()); // width/screen fits the level scale to the screen
		p.scale(camera.getSubScale()); // apply offset for tall screen spaces
		p.translate(-camera.getCenter().x, -camera.getCenter().y); // moves the view around the level

		float currentScale = convert.getScale();

		p.background(240);

		// find corners of camera
		PVector currentTopLeft = convert.screenToLevel(0, 0);
		PVector currentBottomRight = convert.screenToLevel(p.width, p.height);
		// TODO: this system is not great, when I've merged the level view updating code
		// from game to editor, I need to change this so it only goes eliminates things
		// that shouldn't be drawn once

		// draw player and environment

		for (Rectangle r : game.screenObjects) { // draw images
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
//			if (r instanceof Image) {
			((Image) r).draw(p.g, currentScale);
//			}
		}
		for (Rectangle r : game.screenObjects) { // draw tiles on top of images
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
//			if (r instanceof Tile) {
			((Tile) r).draw(p.g, currentScale);
//			}
		}

		if (game.player != null) { // draw the player on top of tiles and images
			game.player.draw(p.g, currentScale);
		}

		if (viewVis) { // draw the views behind events
			for (View view : game.views) {
				view.draw(p.g);
			}
		}
		for (Rectangle r : game.screenObjects) { // draw events on top of player, tiles, and images
			if (r instanceof Event && (eventVis || ((Event) r).visible)) {
				((Event) r).draw(p.g, currentScale);
			}
		}

		game.paper.draw(p.g, game.screenSpace, currentScale);

		// draw tool effects
		if (currentTool != null) {
			currentTool.draw();
		}

		// draw quad tree logic for testing
		if (quadVis && game.player != null) {
			world.draw(p);
			p.fill(0, 0, 0, 150);
			for (Rectangle r : game.playerObjects) {
				p.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			}
//			p.rect(game.player.getPlayerArea().getX(), game.player.getPlayerArea().getY(),
//					game.player.getPlayerArea().getWidth(), game.player.getPlayerArea().getHeight());
		}

		// draw block placement selection if one exists and snapping is turned off
		if (point != null && !snap) {
			p.fill(0, 0, 0, 150);
			p.noStroke();
			p.rect(point.getX(), point.getY(), 100, 100);
			p.fill(0);
			p.textSize(30);
			p.textAlign(LEFT, CENTER);
			int xCoord = (int) point.getX();
			int yCoord = (int) point.getY();
			String s = "[" + xCoord + ", " + yCoord + "]";
			p.text(s, point.getX() + 105, point.getY() + 50);
		}

		// draw selection box around selected object
		if (selected != null && !(selected instanceof Page) && !(controller instanceof PlayerControl)) {
			selected.drawSelected(p.g);
		}

		p.popMatrix(); // start working at screen scale
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
			controller.touchStarted(touch); // Controls for touch started event
		}
	}

	public void touchEnded(PVector touch) {
		editorTop.touchEnded();
		editorBottom.touchEnded();
		editorSide.touchEnded();

		if (nextTouchInactive) {
			nextTouchInactive = false;
		}

		if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
				&& !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
			controller.touchEnded(touch); // Controls for touch moved event
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
			controller.touchMoved(touch, touches); // Controls for touch moved event
		}
	}

	public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
		if (nextTouchInactive) {
			return;
		}
		if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
				&& !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
			controller.onPinch(touches, x, y, d); // controlls for on pinch event
		}
	}

	public void onRotate(float x, float y, float angle) {
		if (nextTouchInactive) {
			return;
		}
		if (controllerActive && !editorTop.insideBoundary(p.mouseX, p.mouseY)
				&& !editorBottom.insideBoundary(p.mouseX, p.mouseY) && !editorSide.insideBoundary(p.mouseX, p.mouseY)) {
			controller.onRotate(x, y, angle); // controlls for on rotate event
		}
	}

	public void onTap(float x, float y) {
		editorBottom.onTap(x, y);
	}

	public void switchView() {
		if (showPageView) {
			showPageView = false;
			// save page view camera
			pvScale = camera.getScale();
			pvSubScale = camera.getSubScale();
			pvCenter.x = camera.getCenter().x;
			pvCenter.y = camera.getCenter().y;
			// set camera to level view
			camera.setScale(lvScale);
			camera.setSubScale(lvSubScale);
			camera.setCenter(lvCenter);
		} else {
			showPageView = true;
			// save level view camera
			lvScale = camera.getScale();
			lvSubScale = camera.getSubScale();
			lvCenter.x = camera.getCenter().x;
			lvCenter.y = camera.getCenter().y;
			// set camera to page view
			camera.setScale(pvScale);
			camera.setSubScale(pvSubScale);
			camera.setCenter(pvCenter);

			// force re-render of pages
			pageView.forceRedraw();
		}
	}

	public enum editorMode {
		ADD, ERASE, SELECT
	}

	public enum imagePlane {
		BACK, LEVEL, FRONT
	}
}
