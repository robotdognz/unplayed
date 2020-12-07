package editor;

import java.util.ArrayList;
import java.util.HashSet;

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
import misc.EditorJSON;
import objects.Image;
import objects.Rectangle;
import objects.Tile;
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
	public Game eGame; // reference to game, same instance of game used everywhere else
	PageView ePageView;
	Quadtree eWorld;
	public Camera eCamera;
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
	public Controller eController; // holds the current controller
	boolean eControllerActive = true; // is the current controller active

	// editor settings
	public boolean snap = true; // things placed in the level will snap to grid
	public editorType eType = editorType.TILE;
	public editorMode eMode = editorMode.ADD;
	imagePlane eImagePlane = imagePlane.LEVEL;
	public boolean pageView = false;

	// current object to put into level
	TileHandler currentTile = null;
	ImageHandler currentImage = null;
	EventHandler currentEvent = null;

	// toolbars
	Toolbar editorTop;
	Toolbar editorBottom;

	// saver/loader class
	public EditorJSON eJSON;

	// frame count and debug visualisation / quadtree
	public boolean debug = false;
	public boolean quadtree = false;
	private int frameDelay = 100;
	private float frame;

	public Editor(PApplet p, TextureCache texture, Game game, Camera camera, Converter convert) {
		this.p = p;
		this.texture = texture;
		this.convert = convert;
		this.eGame = game;
		this.ePageView = eGame.getPageView();
		this.eWorld = eGame.getWorld();
		this.eCamera = camera;
		//this.eTools = new Tools(this, eGame);
		this.eController = new CameraControl(p, this);
		this.editorTop = new EditorTop(p, this);
		this.editorBottom = new EditorBottom(p, this, texture);
		this.eJSON = new EditorJSON(p, texture);

		TOP_DEADZONE = 200;
		BOTTOM_DEADZONE = p.height - 300;

		// Initialize camera backup fields
		lvScale = eCamera.getScale();
		lvSubScale = eCamera.getSubScale();
		lvCenter = new PVector(eCamera.getCenter().x, eCamera.getCenter().y);
		pvScale = eCamera.getScale();
		pvSubScale = eCamera.getSubScale();
		pvCenter = new PVector(eCamera.getCenter().x, eCamera.getCenter().y);
	}

	public void step(ArrayList<PVector> touch) {
		editorTop.step();
		editorBottom.step();

		// step the controller if there are no widget menus open and touch has been
		// reenabled
		if (eControllerActive && !nextTouchInactive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			eController.step(touch); // draw event for controls
		}

		frameCounter();
		if (quadtree) { // update quadtree display in game class
			eGame.quadVis = true;
		} else {
			eGame.quadVis = false;
		}
		if (pageView) { // update pageview display in game class
			eGame.displayPages = true;
			eGame.point = null;
		} else {
			eGame.displayPages = false;
		}

		if (!(eController instanceof EditorControl)) {
			eGame.point = null;
		}

		// figure out what is being placed
	}

	// a bunch of this probably needs to be moved to step, for logical consistency
	// only drawing should be in draw
	public void draw(PVector touch, Menu menu) {
		// draw toolbars
		editorTop.draw(touch, menu);
		editorBottom.draw(touch, menu);

		// draw frame counter and other readouts
		if (debug) {
			p.fill(80);
			p.textSize(50);
			p.textAlign(CENTER, CENTER);
			p.text(PApplet.nf(convert.getScale(), 1, 2), p.width / 2, p.height - editorBottom.getHeight() - 150);
			p.text(eGame.playerObjects.size() + " : " + eGame.screenObjects.size(), p.width / 2,
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
		if (eControllerActive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			eController.touchStarted(touch); // Controls for touch started event
		}
	}

	public void touchEnded() {
		editorTop.touchEnded();
		editorBottom.touchEnded();

		if (nextTouchInactive) {
			nextTouchInactive = false;
		}
	}

	public void touchMoved(ArrayList<PVector> touch) {
		editorTop.touchMoved();
		editorBottom.touchMoved();

		if (nextTouchInactive) { // don't do controller if next touch inactive
			return;
		}
		if (eControllerActive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			eController.touchMoved(touch); // Controls for touch moved event
		}
	}

	public void onPinch(ArrayList<PVector> touch, float x, float y, float d) {
		if (nextTouchInactive) {
			return;
		}
		if (eControllerActive && p.mouseY > TOP_DEADZONE && p.mouseY < BOTTOM_DEADZONE) {
			eController.onPinch(touch, x, y, d); // controlls for on pinch event
		}
	}

	public void onTap(float x, float y) {
		editorBottom.onTap(x, y);
	}

	public void switchView() {
		if (pageView) {
			pageView = false;
			// save page view camera
			pvScale = eCamera.getScale();
			pvSubScale = eCamera.getSubScale();
			pvCenter.x = eCamera.getCenter().x;
			pvCenter.y = eCamera.getCenter().y;
			// set camera to level view
			eCamera.setScale(lvScale);
			eCamera.setSubScale(lvSubScale);
			eCamera.setCenter(lvCenter);
		} else {
			pageView = true;
			// save level view camera
			lvScale = eCamera.getScale();
			lvSubScale = eCamera.getSubScale();
			lvCenter.x = eCamera.getCenter().x;
			lvCenter.y = eCamera.getCenter().y;
			// set camera to page view
			eCamera.setScale(pvScale);
			eCamera.setSubScale(pvSubScale);
			eCamera.setCenter(pvCenter);
		}
	}

	public void editWorld() { // currently does placing and erasing
		switch (eMode) {
		case ADD:
			addObject();
			break;
		case ERASE:
			eraseObject();
			break;
		case SELECT:
			selectObject();
			break;
		}

		if (eGame.point != null && !pageView) {

			int platformX = (int) eGame.point.x;
			int platformY = (int) eGame.point.y;

			boolean spaceFree = true;
			Rectangle foundAtPoint = null;

			// create the new object to put in
			Rectangle toInsert = null;
			if (eType == editorType.TILE && currentTile != null) {
				toInsert = new Tile(texture, currentTile.getFile(), platformX, platformY);
			} else if (eType == editorType.IMAGE && currentImage != null) {
				toInsert = new Image(texture, currentImage.getFile(), platformX, platformY, currentImage.getWidth(),
						currentImage.getHeight());
			} else if (eType == editorType.EVENT && currentEvent != null) {
				toInsert = currentEvent.makeEvent(platformX, platformY);
			} else {
				eGame.point = null; // if there is nothing to put in, remove the point
			}

			// insert it or remove
			if (toInsert != null && eGame.point != null) {
				HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
				eWorld.retrieve(getRectangles, toInsert);
				for (Rectangle p : getRectangles) {

					if (p.getTopLeft().x == platformX && p.getTopLeft().y == platformY
							&& toInsert.getClass().equals(p.getClass())) {
						spaceFree = false;
						foundAtPoint = p;
					}
				}

				if (spaceFree) { // if there isn't something already there
					if (eMode == editorMode.ADD) {
						eWorld.insert(toInsert);
					}
				} else {
					if (eMode == editorMode.ERASE && foundAtPoint != null) {
						eWorld.remove(foundAtPoint);
					}
				}
				eGame.point = null;
			}
		}
	}

	private void addObject() {
//		switch (eType) {
//		case TILE:
//			eTools.addTile();
//			break;
//		case IMAGE:
//			eTools.addImage();
//			break;
//		case EVENT:
//			eTools.addEvent();
//			break;
//		case PAGE:
//			eTools.addPage();
//			break;
//		}
	}

	private void eraseObject() {
//		switch (eType) {
//		case TILE:
//			eTools.eraseTile();
//			break;
//		case IMAGE:
//			eTools.eraseImage();
//			break;
//		case EVENT:
//			eTools.eraseEvent();
//			break;
//		case PAGE:
//			eTools.erasePage();
//			break;
//		}
	}

	private void selectObject() {
//		switch (eType) {
//		case TILE:
//			eTools.selectTile();
//			break;
//		case IMAGE:
//			eTools.selectImage();
//			break;
//		case EVENT:
//			eTools.selectEvent();
//			break;
//		case PAGE:
//			eTools.selectPage();
//			break;
//		}
	}

	public enum editorType {
		TILE, IMAGE, EVENT, PAGE
	}

	public enum editorMode {
		ADD, ERASE, SELECT
	}

	public enum imagePlane {
		BACK, LEVEL, FRONT
	}
}
