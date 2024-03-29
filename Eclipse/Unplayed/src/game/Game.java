package game;

import java.util.ArrayList;
import java.util.HashSet;
import camera.Camera;
import editor.Editor;
import handlers.TextureCache;
import misc.Converter;
import misc.MyContactListener;
import misc.Vibe;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.PlayerStart;
import processing.core.*;
import shiffman.box2d.Box2DProcessing;
import static processing.core.PConstants.*;
import org.jbox2d.callbacks.ContactListener;

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
	public int puzzlesCompleted;
	public ArrayList<View> views;
	public Rectangle startingWorld;
	public HashSet<Rectangle> playerObjects;
	private PageView pageView;
	private PlayerStart playerStart;
	public Tile playerCheckpoint;

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

	// box2d
	public Box2DProcessing box2d;
	public ContactListener contactListener;
	public boolean locked = false;

	// delta time
	float accumulator = 0;
	float stepSize = 1f / 240f;

	public Game(PApplet p, AppLogic app, Camera c, Vibe v, TextureCache texture, Converter convert) {
		// legacy variables from level class TODO: write these out eventually
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

		// box2d
		buildWorld();
	}

	public void buildWorld() {
		box2d = new Box2DProcessing(p);
		box2d.createWorld();
		box2d.setGravity(0, -400);
		box2d.world.setAutoClearForces(false);

		// contact listener
		contactListener = new MyContactListener(this);
		box2d.world.setContactListener(contactListener);
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

	public void clearPlayerStart() {
		// fully remove the player
		if (this.player != null) {
			this.player.destroy();
		}
		this.player = null;
		this.playerStart = null;
	}

	public Rectangle getPlayerStart() {
		return playerStart;
	}

	public void startGame() {
		// only alter the camera if we're in game or play testing
		if (playerStart != null && camera.getGame()) {
			// setup start camera area
			cameraAreaStart = playerStart.getCameraArea();

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

			// make sure the camera sub scale is correct
			if ((cameraArea.getBottomRight().y - cameraArea.getTopLeft().y)
					/ (cameraArea.getBottomRight().x - cameraArea.getTopLeft().x) > (float) p.height
							/ (float) p.width) {
				// set the new sub scale
				newSubScale = ((float) p.height
						/ ((float) p.width / (float) (cameraArea.getBottomRight().x - cameraArea.getTopLeft().x)))
						/ (cameraArea.getBottomRight().y - cameraArea.getTopLeft().y);

			} else {
				newSubScale = 1;
			}
			// set the scale
			camera.setSubScale(newSubScale);
		}
		// clear player
		if (this.player != null) {
			this.player.destroy();
		}
		player = null;
		// clear checkpoint
		playerCheckpoint = null;
		// reset removed and placed tiles
		for (Tile t : placed) {
			world.remove(t);
		}

		for (Tile t : removed) {
			// don't insert tiles that were created by PlayerEnds
			if (placed.contains(t)) {
				continue;
			}
			world.insert(t);
		}
		placed.clear();
		removed.clear();

		// initialise player
		if (playerStart != null) {
			createPlayer(playerStart);
		}

		puzzlesCompleted = 0;

	}

	public void endGame() {
		Editor editor = app.getEditor();

		if (editor == null) { // in a normal game
			p.delay(180);
			app.nextLevel();
		} else { // in the editor
			editor.toast.showToast("Level Complete");
			p.delay(400);
			startGame();
		}
	}

	public void endPuzzle(Rectangle playerArea) {
		
		if(world.playerEndCount() - puzzlesCompleted == 1) {
			endGame();
			return;
		}
		
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
		// if the next block the player will become has been found
		if (found != null) {
			// pause
			p.delay(180);

			// update the checkpoints
			this.playerCheckpoint = found;
			this.cameraAreaCheckpoint = cameraArea.copy();

			// make the matching tile to fill the slot
			int tileX = (int) (Math.round((player.getCenter().x - player.getWidth() / 2) / 10) * 10);
			int tileY = (int) (Math.round((player.getCenter().y - player.getHeight() / 2) / 10) * 10);
			Tile newTile = new Tile(box2d, texture, player.getFile(), tileX, tileY);
			newTile.setAngle(player.getAdjustedAngle());
			// insert the new tile into the world and add it to placed
			world.insert(newTile);
			placed.add(newTile);

			// create the new player
			puzzlesCompleted++;
			createPlayer(found);
		}

	}

	public void createPlayer(Rectangle playerArea) {
		if (playerArea instanceof PlayerStart) {
			Tile current = ((PlayerStart) playerArea).getRequired();
			if (this.player != null) {
				this.player.destroy();

			}
			player = new Player(p, box2d, locked, texture, current, vibe);
		} else if (playerArea instanceof Tile) {
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
				removed.add(found);
				world.remove(found);
				if (playerCheckpoint != null) {
					if (this.player != null) {
						this.player.destroy();
					}
					player = new Player(p, box2d, locked, texture, playerCheckpoint, vibe);
				} else if (playerStart != null) {
					Tile current = ((PlayerStart) playerStart).getRequired();
					if (current != null) {
						if (this.player != null) {
							this.player.destroy();
						}
						player = new Player(p, box2d, locked, texture, current, vibe);
					}
				}

			}
		}
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
			newCenter = new PVector(centerX, centerY); // new center
			newCameraArea = cameraAreaCheckpoint.copy(); // new camera area
		} else if (cameraAreaStart != null) { // if there is a camera start
			// calculate values
			PVector cameraTopLeft = cameraAreaStart.getTopLeft();
			PVector cameraBottomRight = cameraAreaStart.getBottomRight();
			int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
			int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
			// apply values
			newScale = cameraAreaStart.getWidth(); // new scale
			newCenter = new PVector(centerX, centerY); // new center
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

	public void step(float deltaTime) {
		// step player non-physics logic
		if (player != null) {
			player.step();
		}

		// step physics
		int steps = calculateSteps(deltaTime);
		while (steps > 0) {
			if (player != null) {
				player.physicsStep(stepSize);
			}
			box2d.step(stepSize, 8, 3);
			steps--;
		}
		box2d.world.clearForces();

		// get objects to draw
		screenObjects.clear();
		world.retrieve(screenObjects, screenSpace);

		if (camera.getGame()) {
			screenMovement(deltaTime);
		}
		PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
		float screenSpaceWidth = convert.screenToLevel(p.width + screenSpaceOffset * 2);
		float screenSpaceHeight = convert.screenToLevel(p.height + screenSpaceOffset * 2);
		screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);

		pageView.step(); // step the page view
	}

	void screenMovement(float deltaTime) {
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
			camera.setSubScale(PApplet.lerp(camera.getSubScale(), newSubScale, PApplet.exp(-(zoomSpeed / deltaTime)))); // -zoomSpeed
		}
		// main scale
		if (camera.getScale() != newScale) {
			camera.setScale(PApplet.lerp(camera.getScale(), newScale, PApplet.exp(-(zoomSpeed / deltaTime))));
		}
		// translate
		if (camera.getCenter() != newCenter) {
			camera.setCenter(PVector.lerp(camera.getCenter(), newCenter, PApplet.exp(-(zoomSpeed / deltaTime))));
		}
		// black border movement
		if (!cameraArea.sameDimensions(newCameraArea)) {
			float topLeftX = PApplet.lerp(cameraArea.getTopLeft().x, newCameraArea.getTopLeft().x,
					PApplet.exp(-(boarderZoomSpeed / deltaTime))); // -boarderZoomSpeed
			float topLeftY = PApplet.lerp(cameraArea.getTopLeft().y, newCameraArea.getTopLeft().y,
					PApplet.exp(-(boarderZoomSpeed / deltaTime)));
			float bottomRightX = PApplet.lerp(cameraArea.getBottomRight().x, newCameraArea.getBottomRight().x,
					PApplet.exp(-(boarderZoomSpeed / deltaTime)));
			float bottomRightY = PApplet.lerp(cameraArea.getBottomRight().y, newCameraArea.getBottomRight().y,
					PApplet.exp(-(boarderZoomSpeed / deltaTime)));
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

	private int calculateSteps(float elapsed) {
		// Our simulation frequency is 240Hz, a (four one sixth) ms period.

		// We will pretend our display sync rate is one of these:
		if (elapsed > 7.5 * stepSize)
			return 8; // 30 Hz ( .. to 32 Hz )
		else if (elapsed > 6.5 * stepSize)
			return 7; // 34.29 Hz ( 32 Hz to 36.92 Hz )
		else if (elapsed > 5.5 * stepSize)
			return 6; // 40 Hz ( 36.92 Hz to 43.64 Hz )
		else if (elapsed > 4.5 * stepSize)
			return 5; // 48 Hz ( 43.64 Hz to 53.33 Hz )
		else if (elapsed > 3.5 * stepSize)
			return 4; // 60 Hz ( 53.33 Hz to 68.57 Hz )
		else if (elapsed > 2.5 * stepSize)
			return 3; // 90 Hz ( 68.57 Hz to 96 Hz )
		else if (elapsed > 1.5 * stepSize)
			return 2; // 120 Hz ( 96 Hz to 160 Hz )
		else
			return 1; // 240 Hz ( 160 Hz to .. )
	}

//	public int getRemainingPuzzles() {
//		return world.playerEndCount() - puzzlesCompleted;
//	}

}
