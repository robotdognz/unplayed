package editor;

import java.util.ArrayList;
import camera.Camera;
import controllers.CameraControl;
import controllers.Controller;
import controllers.EditorControl;
import game.Game;
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
import objects.Rectangle;
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
	//Tools eTools;

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
	
	//selected object
	Rectangle selected;

	// toolbars
	Toolbar editorTop;
	Toolbar editorBottom;

	// saver/loader class
	public EditorJSON eJSON;

	// frame count and debug visualization / quadtree
	public boolean debug = false;
	public boolean quadtree = false;
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
		if (quadtree) { // update quadtree display in game class
			game.quadVis = true;
		} else {
			game.quadVis = false;
		}
		if (showPageView) { // update pageview display in game class
			game.displayPages = true;
			game.point = null;
		} else {
			game.displayPages = false;
		}

		if (!(controller instanceof EditorControl)) {
			game.point = null;
		}

		game.selected = selected;
	}

	// a bunch of this probably needs to be moved to step, for logical consistency
	// only drawing should be in draw
	public void draw(PVector touch, Menu menu) {
		// draw toolbars
		editorTop.draw(touch, menu);
		editorBottom.draw(touch, menu);
		
		//draw tool effects
		if(currentTool != null) {
			currentTool.draw();
		}

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

	public void touchEnded() {
		editorTop.touchEnded();
		editorBottom.touchEnded();

		if (nextTouchInactive) {
			nextTouchInactive = false;
		}
		
		if(currentTool != null) {
			currentTool.touchEnded();
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

	public void editWorld() {
		currentTool.activate();
	}

	public enum editorMode {
		ADD, ERASE, SELECT
	}

	public enum imagePlane {
		BACK, LEVEL, FRONT
	}
}
