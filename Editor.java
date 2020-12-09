package editor;

import java.util.ArrayList;
import camera.Camera;
import controllers.CameraControl;
import controllers.Controller;
import controllers.EditorControl;
import game.Game;
import game.Page;
import game.PageView;
import game.Quadtree;
import handlers.EventHandler;
import handlers.ImageHandler;
import handlers.TextureCache;
import handlers.TileHandler;
import menus.Menu;
import misc.Converter;
import misc.DoToast;
import misc.EditorJSON;
import objects.Event;
import objects.Image;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import processing.core.*;
import static processing.core.PConstants.*;

public class Editor {
	// touch constraint variables
	public int TOP_DEADZONE; // TODO: needs to scale with screen
	public int BOTTOM_DEADZONE; // TODO: needs to scale with screen
	public boolean nextTouchInactive = false;

	PApplet p;
	TextureCache texture;
	Converter convert;
	public Game game; // reference to game, same instance of game used everywhere else
	PageView pageView;
	Quadtree world;
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
	boolean controllerActive = true; // is the current controller active
	public PVector point = null; // holds the current selection point in the game world
	public boolean eventVis; // are events visible
	public boolean quadVis; // is the quad tree being draw

	// editor settings
	public boolean snap = true; // things placed in the level will snap to grid
	public Tool currentTool;
	public editorMode eMode;
	public imagePlane eImagePlane;
	public boolean showPageView = false;

	// current object to put into level
	TileHandler currentTile = null;
	ImageHandler currentImage = null;
	EventHandler currentEvent = null;
	View currentView = null;

	// selected object
	Rectangle selected;

	// toolbars
	Toolbar editorTop;
	Toolbar editorBottom;

	// saver/loader class
	public EditorJSON eJSON;

	// frame count and debug visualization / quadtree
	public boolean debug = false;
	// public boolean quadtree = false;
	private int frameDelay = 100;
	private float frame;

	public Editor(PApplet p, TextureCache texture, Game game, Camera camera, Converter convert, DoToast toast) {
		this.p = p;
		this.texture = texture;
		this.convert = convert;
		this.game = game;
		this.pageView = game.getPageView();
		this.world = game.getWorld();
		this.camera = camera;
		this.controller = new CameraControl(p, this);
		this.editorTop = new EditorTop(p, this);
		this.editorBottom = new EditorBottom(p, this, texture);
		this.eJSON = new EditorJSON(p, texture, toast);

		this.currentTool = new TileTool(this);
		this.eMode = editorMode.ADD;
		this.eImagePlane = imagePlane.LEVEL;
		this.eventVis = true;
		this.quadVis = false;

		TOP_DEADZONE = 200;
		BOTTOM_DEADZONE = p.height - 300;

		// Initialize camera backup fields
		lvScale = camera.getScale();
		lvSubScale = camera.getSubScale();
		lvCenter = new PVector(camera.getCenter().x, camera.getCenter().y);
		pvScale = camera.getScale();
		pvSubScale = camera.getSubScale();
		pvCenter = new PVector(camera.getCenter().x, camera.getCenter().y);
	}

	public void step(ArrayList<PVector> touch) {
		editorTop.step();
		editorBottom.step();

		// step the controller if there are no widget menus open and touch has been
		// reenabled
		if (controllerActive && !nextTouchInactive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			controller.step(touch); // draw event for controls
		}

		frameCounter();

		if (showPageView) { // update pageview display in game class
			game.displayPages = true;
			point = null;
		} else {
			game.displayPages = false;
		}

		if (!(controller instanceof EditorControl)) {
			point = null;
		}

	}

	// a bunch of this probably needs to be moved to step, for logical consistency
	// only drawing should be in draw
	public void draw(PVector touch, Menu menu) {
		// draw the level
		if (!showPageView) {
			drawLevel();
		}

		// draw toolbars
		editorTop.draw(touch, menu);
		editorBottom.draw(touch, menu);
		// TODO: editorSide.draw(touch, menu);

		// draw frame counter and other readouts
		if (debug) {
			p.fill(80);
			p.textSize(50);
			p.textAlign(CENTER, CENTER);
			p.text(PApplet.nf(convert.getScale(), 1, 2), p.width / 2, p.height - editorBottom.getHeight() - 150);
			p.text(game.playerObjects.size() + " : " + game.screenObjects.size(), p.width / 2,
					p.height - editorBottom.getHeight() - 100);
			p.text("FPS: " + PApplet.nf(this.frame, 1, 2), p.width / 2, p.height - editorBottom.getHeight() - 50);
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

		// draw player and environment
		p.background(240);
		for (Rectangle r : game.screenObjects) { // draw images
			if (r instanceof Image) {
				((Image) r).draw(p.g, currentScale);
			}
		}
		for (Rectangle r : game.screenObjects) { // draw tiles and events on top of images
			if (r instanceof Tile) {
				((Tile) r).draw(p.g, currentScale);
			}
			if (r instanceof Event && (eventVis || ((Event) r).visible)) {
				((Event) r).draw(p.g, currentScale);
			}
		}
		game.player.draw(p.g);
		game.paper.draw(p.g, game.screenSpace, currentScale);

		// draw the page views TODO: remove this
//		for (Page page : pageView.getPages()) {
//			Rectangle pView = page.getView();
//			p.noFill();
//			p.stroke(255, 0, 0);
//			p.strokeWeight(4);
//			p.rect(pView.getX(), pView.getY(), pView.getWidth(), pView.getHeight());
//		}

		// draw the views
		for (View view : game.views) {
			p.noFill();
			p.stroke(255, 0, 0);
			p.strokeWeight(4);
			p.rect(view.getX(), view.getY(), view.getWidth(), view.getHeight());
		}
		p.noStroke();

		// draw tool effects
		if (currentTool != null) {
			currentTool.draw();
		}

		// draw quad tree logic for testing
		if (quadVis) {
			world.draw(p);
			p.fill(0, 0, 0, 150);
			for (Rectangle r : game.playerObjects) {
				p.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			}
			p.rect(game.player.getPlayerArea().getX(), game.player.getPlayerArea().getY(),
					game.player.getPlayerArea().getWidth(), game.player.getPlayerArea().getHeight());
		}

		// draw block placement selection if one exists and snapping is turned off
		if (point != null && !snap) {
			p.fill(0, 0, 0, 150);
			p.rect(point.x, point.y, 100, 100);
			p.fill(0);
			p.textSize(30);
			p.textAlign(LEFT, CENTER);
			int xCoord = (int) point.x;
			int yCoord = (int) point.y;
			String s = "[" + xCoord + ", " + yCoord + "]";
			p.text(s, point.x + 105, point.y + 50);
		}

		// draw selection box around selected object
		if (selected != null) {
			p.noFill();
			p.stroke(255, 0, 0); // selection colour
			p.strokeWeight(2);
			p.rect(selected.getX(), selected.getY(), selected.getWidth(), selected.getHeight());
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
		if (controllerActive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			controller.touchStarted(touch); // Controls for touch started event
		}
	}

	public void touchEnded(PVector touch) {
		editorTop.touchEnded();
		editorBottom.touchEnded();

		if (nextTouchInactive) {
			nextTouchInactive = false;
		}

		if (controllerActive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			controller.touchEnded(touch); // Controls for touch moved event
		}
		
	}

	public void touchMoved(ArrayList<PVector> touch) {
		editorTop.touchMoved(touch);
		editorBottom.touchMoved(touch);

		if (nextTouchInactive) { // don't do controller if next touch inactive
			return;
		}
		if (controllerActive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			controller.touchMoved(touch); // Controls for touch moved event
		}
	}

	public void onPinch(ArrayList<PVector> touch, float x, float y, float d) {
		if (nextTouchInactive) {
			return;
		}
		if (controllerActive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			controller.onPinch(touch, x, y, d); // controlls for on pinch event
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
		}
	}

	public enum editorMode {
		ADD, ERASE, SELECT
	}

	public enum imagePlane {
		BACK, LEVEL, FRONT
	}
}
