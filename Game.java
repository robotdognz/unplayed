package game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import camera.Camera;
import editor.Editor;
import handlers.TextureCache;
import misc.Converter;
import misc.Vibe;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.PlayerStart;
import processing.core.*;
import static processing.core.PConstants.*;

public class Game {
	private PApplet p;
	public Player player;
	public Paper paper;
	public Converter convert;
	private Vibe vibe;
	private TextureCache texture;
	public AppLogic app;

	public Quadtree world;
	public ArrayList<Tile> removed; // holds the tiles that the player has become and have been removed from the
									// world
	public ArrayList<Tile> placed; // holds the tiles the player has left behind after slotting in
	public ArrayList<View> views;
	public Rectangle startingWorld;
	public HashSet<Rectangle> playerObjects;
	private PageView pageView;
	private PlayerStart playerStart;
	private Rectangle playerCheckpoint;

	public Camera camera;
	public Rectangle cameraAreaStart;
	public Rectangle cameraAreaCheckpoint;

	// TODO: move these to editor (need to move some logic from step too)
	public Rectangle screenSpace;
	public int screenSpaceOffset;
	public HashSet<Rectangle> screenObjects;

	// local variables for camera
	public float newScale;
	public PVector newCenter;
	public float zoomSpeed = 0.1f; // 0.1 is the default

	// local variables for camera tall screen space
	private float newSubScale = 1;

	// variables for black border
	public Rectangle cameraArea;
	public Rectangle newCameraArea;
	public float boarderZoomSpeed = 0.1f; // 0.1 is default

	public Game(PApplet p, AppLogic app, Camera c, Vibe v, TextureCache texture, Converter convert) {
		// legacy variables from level class TODO: write these out eventually
//		playerStart = new Rectangle(0, 0, 100, 100);
		PVector cameraTopLeft = new PVector(-400, -400);
		PVector cameraBottomRight = new PVector(500, 600);
		int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
		int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
		PVector startCenter = new PVector(centerX, centerY);
		int startScale = (int) Math.abs(cameraBottomRight.x - cameraTopLeft.x);
		int bottomOfTopBar = (int) cameraTopLeft.y;
		int topOfBottomBar = (int) cameraBottomRight.y;

		// actual game class starts here
		this.p = p;
		this.app = app;
		this.camera = c;
		this.vibe = v;
		this.texture = texture;
		this.convert = convert;

		startingWorld = new Rectangle(0 - 400, 0 - 400, 900, 900);
		world = new Quadtree(startingWorld);
		removed = new ArrayList<Tile>();
		placed = new ArrayList<Tile>();
		views = new ArrayList<View>();
		playerObjects = new HashSet<Rectangle>();
		player = null;

		pageView = new PageView(p, this, camera, texture, convert);

		paper = new Paper(texture);

		// camera
		camera.setScale(startScale);
		newScale = startScale;
		camera.setCenter(startCenter);
		newCenter = new PVector(camera.getCenter().x, camera.getCenter().y);

		// calculate screen space TODO: clean this up, screen space should be moved to
		// the editor
		screenSpaceOffset = 0; // positive makes it larger, negative makes it smaller
		PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
		float screenSpaceWidth = convert.screenToLevel(p.width + screenSpaceOffset * 2);
		float screenSpaceHeight = convert.screenToLevel(p.height + screenSpaceOffset * 2);
		screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);
		screenObjects = new HashSet<Rectangle>();

		float camX = camera.getCenter().x - newScale / 2;
		cameraArea = new Rectangle(camX, bottomOfTopBar, newScale, topOfBottomBar - bottomOfTopBar);
		newCameraArea = cameraArea.copy();
	}

	public void setPlayerStart(PlayerStart start) {
		if (start != null) {
			// set player start
			playerStart = start;
			// set start camera
			cameraAreaStart = start.getCameraArea();
			createPlayer(start);
		}
	}

	public Rectangle getPlayerStart() {
		return playerStart;
	}

	public void startGame() {
		// only alter the camera if we're in game or play testing
		if (cameraAreaStart != null && camera.getGame()) {
			// calculate values
			PVector cameraTopLeft = cameraAreaStart.getTopLeft();
			PVector cameraBottomRight = cameraAreaStart.getBottomRight();
			int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
			int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
			PVector startCenter = new PVector(centerX, centerY);
			// apply values
			camera.setScale(cameraAreaStart.getWidth()); // scale
			newScale = cameraAreaStart.getWidth(); // new scale
			camera.setCenter(startCenter); // centre
			newCenter = new PVector(camera.getCenter().x, camera.getCenter().y); // new centre
			cameraArea = cameraAreaStart.copy(); // camera area
			newCameraArea = cameraArea.copy(); // new camera area
		}
		// clear player
		player = null;
		// clear checkpoint
		playerCheckpoint = null;
		// reset removed and placed tiles
		for (Tile t : placed) {
			world.remove(t);
		}
		placed.clear();
		for (Tile t : removed) {
			world.insert(t);
		}
		removed.clear();

		// initialise player
		if (playerStart != null) {
			createPlayer(playerStart);
		}

	}

	public void endGame() {
		Editor editor = app.getEditor();

		if (editor == null) { // in a normal game
			// TODO: load next level, etc.
		} else { // in the editor
			editor.toast.showToast("Level Complete");
			p.delay(400);
			startGame();
		}
	}

	public void endPuzzle(Rectangle playerArea) {
		// pause
		p.delay(180);

		HashSet<Rectangle> returnObjects = new HashSet<Rectangle>();
		world.retrieve(returnObjects, playerArea);
		Tile found = null;
		for (Rectangle r : returnObjects) {
			if (!(r instanceof Tile)) {
				continue;
			}
			if (r.getTopLeft().x > playerArea.getBottomRight().x - 1) {
				continue;
			}
			if (r.getBottomRight().x < playerArea.getTopLeft().x + 1) {
				continue;
			}
			if (r.getTopLeft().y > playerArea.getBottomRight().y - 1) {
				continue;
			}
			if (r.getBottomRight().y < playerArea.getTopLeft().y + 1) {
				continue;
			}

			found = ((Tile) r);
		}
		if (found != null) {
			// update the checkpoints
			this.playerCheckpoint = found.copy();
			this.cameraAreaCheckpoint = cameraArea.copy();
			// fill the slot with a matching tile
			Tile newTile = new Tile(texture, player.getFile(), player.getX(), player.getY());
			if (player.isFlippedH()) {
				newTile.flipH();
			}
			if (player.isFlippedV()) {
				newTile.flipV();
			}
			newTile.setAngle(player.getAngle());
			world.insert(newTile);
			placed.add(newTile);
			createPlayer(found);
		}

//		// update the checkpoints
//		this.playerCheckpoint = playerArea.copy();
//		this.cameraAreaCheckpoint = cameraArea.copy();
//		// fill the slot with a matching tile
//		Tile newTile = new Tile(texture, player.getFile(), player.getX(), player.getY());
//		world.insert(newTile);
//		placed.add(newTile);
//		createPlayer(playerArea);
	}

	public void createPlayer(Rectangle playerArea) {
		if (playerArea instanceof PlayerStart) {
			Tile current = ((PlayerStart) playerArea).getRequired();
			player = new Player(p, texture, current, vibe);
		} else if (playerArea instanceof Tile) {
			Tile current = (Tile) playerArea;
			removed.add(current);
			world.remove(current);
			player = new Player(p, texture, current, vibe);
		}
//		else {
//			HashSet<Rectangle> returnObjects = new HashSet<Rectangle>();
//			ArrayList<Tile> newPlayer = new ArrayList<Tile>();
//			world.retrieve(returnObjects, playerArea);
//			for (Rectangle r : returnObjects) {
//				if (!(r instanceof Tile)) {
//					continue;
//				}
//				if (r.getTopLeft().x > playerArea.getBottomRight().x - 1) {
//					continue;
//				}
//				if (r.getBottomRight().x < playerArea.getTopLeft().x + 1) {
//					continue;
//				}
//				if (r.getTopLeft().y > playerArea.getBottomRight().y - 1) {
//					continue;
//				}
//				if (r.getBottomRight().y < playerArea.getTopLeft().y + 1) {
//					continue;
//				}
//
//				newPlayer.add((Tile) r);
//			}
//			if (newPlayer.size() > 0) {
//				File file = newPlayer.get(0).getFile(); // TODO: needs to be able to do more than just one tile file
//				removed.add(newPlayer.get(0));
//				world.remove(newPlayer.get(0));
//				if (playerCheckpoint != null) {
//					player = new Player(p, texture, file, playerCheckpoint.getX(), playerCheckpoint.getY(), vibe);
//				} else if (playerStart != null) {
//					player = new Player(p, texture, file, playerStart.getX(), playerStart.getY(), vibe);
//				}
//			}
//		}

	}

	public void stopPlayer() {
		if (player != null) {
			player.still();
		}
	}

	public void restart() {
		if (playerCheckpoint != null) { // if there is a player checkpoint
			Rectangle previousPlayer = removed.get(removed.size() - 1);
			removed.remove(previousPlayer);
			world.insert(previousPlayer);
			createPlayer(playerCheckpoint);
		} else if (playerStart != null) { // if there is a player start
//			Rectangle previousPlayer = removed.get(removed.size() - 1);
//			removed.remove(previousPlayer);
//			world.insert(previousPlayer);
			createPlayer(playerStart);
		}
		if (cameraAreaCheckpoint != null) { // if there is a camera checkpoint
			// calculate values
			PVector cameraTopLeft = cameraAreaCheckpoint.getTopLeft();
			PVector cameraBottomRight = cameraAreaCheckpoint.getBottomRight();
			int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
			int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
			// apply values
			newScale = cameraAreaCheckpoint.getWidth(); // new scale
			newCenter = new PVector(centerX, centerY); // new centre
			newCameraArea = cameraAreaCheckpoint.copy(); // new camera area
		} else if (cameraAreaStart != null) { // if there is a camera start
			// calculate values
			PVector cameraTopLeft = cameraAreaStart.getTopLeft();
			PVector cameraBottomRight = cameraAreaStart.getBottomRight();
			int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
			int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
			// apply values
			newScale = cameraAreaStart.getWidth(); // new scale
			newCenter = new PVector(centerX, centerY); // new centre
			newCameraArea = cameraAreaStart.copy(); // new camera area
		}
	}

	public void draw() {
		pageView.draw();

		// camera black border code
		p.pushMatrix(); // start working at game scale
		p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen

		// camera
		p.scale((float) p.width / (float) camera.getScale()); // width/screen fits the level scale to the screen
		p.scale(camera.getSubScale()); // apply offset for tall screen spaces
		p.translate(-camera.getCenter().x, -camera.getCenter().y); // moves the view around the level

		// draw black bars
		if (camera.getGame()) {
			if (player != null) {
				player.drawArrows(this);
			}
			p.fill(20, 255); // 10, 255
			int barSize = 1000000;
			p.rectMode(CORNERS);
			p.noStroke();

			// top bar
			p.rect(-barSize + camera.getCenter().x, camera.getCenter().y - barSize, barSize + camera.getCenter().x,
					cameraArea.getTopLeft().y);
			// bottom bar
			p.rect(-barSize + camera.getCenter().x, cameraArea.getBottomRight().y, barSize + camera.getCenter().x,
					camera.getCenter().y + barSize);
			// left bar
			p.rect(-barSize + camera.getCenter().x, camera.getCenter().y - barSize, cameraArea.getTopLeft().x,
					camera.getCenter().y + barSize);
			// right bar
			p.rect(cameraArea.getBottomRight().x, camera.getCenter().y - barSize, barSize + camera.getCenter().x,
					camera.getCenter().y + barSize);
			p.rectMode(CORNER);
		}

		p.popMatrix(); // start working at screen scale
	}

	public void step() {
		screenObjects.clear();
		world.retrieve(screenObjects, screenSpace);

		// find platforms near the player
		if (player != null) {
			playerObjects.clear();
			world.retrieve(playerObjects, player.getPlayerArea());
			player.step(playerObjects, this);
		}

		if (camera.getGame()) {
			screenMovement();
		}
		PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
		float screenSpaceWidth = convert.screenToLevel(p.width + screenSpaceOffset * 2);
		float screenSpaceHeight = convert.screenToLevel(p.height + screenSpaceOffset * 2);
		screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);

		pageView.step(); // step the page view
	}

	void screenMovement() {
		// tall screen space scaling
		// uses the 'new...' versions of edge variables so that
		// scaling happens immediately
		if (camera.getScale() != newScale || !cameraArea.sameDimensions(newCameraArea)) {
			// if there might be a difference in tall screen scale
			if ((newCameraArea.getBottomRight().y - newCameraArea.getTopLeft().y)
					/ (newCameraArea.getBottomRight().x - newCameraArea.getTopLeft().x) > (float) p.height
							/ (float) p.width) {
				newSubScale = ((float) p.height
						/ ((float) p.width / (float) (newCameraArea.getBottomRight().x - newCameraArea.getTopLeft().x)))
						/ (newCameraArea.getBottomRight().y - newCameraArea.getTopLeft().y);
			} else {
				newSubScale = 1;
			}
		}

		if (camera.getSubScale() != newSubScale) {
			camera.setSubScale(PApplet.lerp(camera.getSubScale(), newSubScale, PApplet.exp(-zoomSpeed)));
		}
		// main scale
		if (camera.getScale() != newScale) {
			camera.setScale(PApplet.lerp(camera.getScale(), newScale, PApplet.exp(-zoomSpeed)));
		}
		// translate
		if (camera.getCenter() != newCenter) {
			camera.setCenter(PVector.lerp(camera.getCenter(), newCenter, PApplet.exp(-zoomSpeed)));
		}
		// black border movement
		if (!cameraArea.sameDimensions(newCameraArea)) {
			float topLeftX = PApplet.lerp(cameraArea.getTopLeft().x, newCameraArea.getTopLeft().x,
					PApplet.exp(-boarderZoomSpeed));
			float topLeftY = PApplet.lerp(cameraArea.getTopLeft().y, newCameraArea.getTopLeft().y,
					PApplet.exp(-boarderZoomSpeed));
			float bottomRightX = PApplet.lerp(cameraArea.getBottomRight().x, newCameraArea.getBottomRight().x,
					PApplet.exp(-boarderZoomSpeed));
			float bottomRightY = PApplet.lerp(cameraArea.getBottomRight().y, newCameraArea.getBottomRight().y,
					PApplet.exp(-boarderZoomSpeed));
			cameraArea.setCorners(topLeftX, topLeftY, bottomRightX, bottomRightY);
		}

	}

	public Player getPlayer() {
		return player;
	}

	public Quadtree getWorld() {
		return world;
	}

	public PageView getPageView() {
		return pageView;
	}

	public View getView(float x, float y) {
		if (views.size() < 1) {
			return null;
		}
		View best = null; // best found match
		for (View view : views) {
			if (view.getTopLeft().x > x) {
				continue;
			}
			if (view.getBottomRight().x < x) {
				continue;
			}
			if (view.getTopLeft().y > y) {
				continue;
			}
			if (view.getBottomRight().y < y) {
				continue;
			}

			// find the view that on top
			if (best != null) {
				if (view.getX() > best.getX() || view.getY() > best.getY()) {
					best = view;
				} else if (view.getWidth() < best.getWidth() || view.getHeight() < best.getHeight()) {
					best = view;
				}
			} else {
				best = view;
			}
		}
		return best;
	}

	public void setViews(ArrayList<View> views) {
		this.views.clear();
		for (View view : views) {
			this.views.add(view);
		}
	}

}
