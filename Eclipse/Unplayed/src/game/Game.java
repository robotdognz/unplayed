package game;

import java.util.ArrayList;
import java.util.HashSet;
import camera.Camera;
import game.player.Player;
import handlers.LoadingHandler;
import handlers.TextureCache;
import misc.Converter;
import misc.CountdownTimer;
import misc.MyContactListener;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import processing.core.*;
import shiffman.box2d.Box2DProcessing;
import org.jbox2d.callbacks.ContactListener;

public class Game {
	private PApplet p;
	public Player player;
	public MathsPaper paper;
	public Converter convert;
	private TextureCache texture;

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

	private CountdownTimer pauseTimer; // used to pause game during puzzles
	private PauseType pauseType;
	private Rectangle playerAreaTemp;

	public LoadingHandler currentLoading = null;

	private enum PauseType {
		NEXT_LEVEL, RESTART_LEVEL, NEXT_PLAYER, NONE
	}; // used to indicate what type of pause has happened

	public Camera camera;

	public Rectangle screenSpace;
	public int screenSpaceOffset;
	// screenSpaceOffset is for debug purposes, I believe (future Marco here), it
	// can be used to decrease the rendering space

	// box2d
	public Box2DProcessing box2d;
	public ContactListener contactListener;

//	public boolean tumble = true; // should the player tumble when being corrected into a slot

	// delta time
	float accumulator = 0;
	float stepSize = 1f / 240f;

	public Game(PApplet p, Camera c, TextureCache texture, Converter convert) {
		this.p = p;
//		this.app = app;
		this.camera = c;
		this.texture = texture;
		this.convert = convert;

		startingWorld = new Rectangle(0 - 400, 0 - 400, 900, 900);
		world = new Quadtree(startingWorld);
		removed = new ArrayList<Tile>();
		placed = new ArrayList<Tile>();
		views = new ArrayList<View>();
		playerObjects = new HashSet<Rectangle>();
		player = null;

		pageView = new PageView(p, this, texture, convert);

		paper = new MathsPaper();

		pauseTimer = new CountdownTimer(0.4f);
		pauseType = PauseType.NONE;

		// calculate screen space
		screenSpaceOffset = 0; // positive makes it larger, negative makes it smaller
		PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
		float screenSpaceWidth = convert.screenToLevel(p.width + screenSpaceOffset * 2);
		float screenSpaceHeight = convert.screenToLevel(p.height + screenSpaceOffset * 2);
		screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);

		// box2d
		buildWorld();
	}

	public void emptyGame() {
		world.clear(); // remove old world objects
		placed.clear(); // removed tiles that have been inserted into slots
		removed.clear(); // remove tiles that have become the player
		clearPlayerStart(); // remove the player
		playerCheckpoint = null; /// remove checkpoint
		views.clear();
		pageView.clearPageViewObjects(); // remove pages and backgrounds
		buildWorld(); // rebuild world

		currentLoading = null;
	}

	public void removePlayer() {
		if (player == null) {
			return;
		}
		player.destroy(); // remove the player physics body
		player = null; // remove the player
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
			// create new player
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
		// clear player
		if (this.player != null) {
			this.player.destroy();
			player = null;
		}

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

		// reset player ends
		HashSet<Rectangle> allObjects = new HashSet<Rectangle>();
		world.getAll(allObjects);
		for (Rectangle temp : allObjects) {
			if (temp instanceof PlayerEnd) {
				((PlayerEnd) temp).reset();
			}
		}

		// Initialize player
		if (playerStart != null) {
			createPlayer(playerStart);
		}

		puzzlesCompleted = 0;

	}

	public void endGame() {
		// if already in the process of doing something, return
		if (pauseTimer.isRunning()) {
			return;
		}

//		Editor editor = AppLogic.getEditor();

		if (AppLogic.getEditor() == null) { // in a normal game
			pauseTimer.start();
			pauseType = PauseType.NEXT_LEVEL;
		} else { // in the editor
			if (AppLogic.editorToggle) {
//				editor.toast.showToast("Level Complete");
				AppLogic.toast.showToast("Level Complete");
			}
			pauseTimer.start();
			pauseType = PauseType.RESTART_LEVEL;
		}
	}

	public void endPuzzle(Rectangle playerArea) {
		if (world.playerEndCount() - puzzlesCompleted == 1) {
			endGame();
			return;
		}

		pauseTimer.start();
		pauseType = PauseType.NEXT_PLAYER;
		playerAreaTemp = playerArea.copy();
	}

	private void nextPlayer() {
		HashSet<Rectangle> returnObjects = new HashSet<Rectangle>();
		world.retrieve(returnObjects, playerAreaTemp);
		Tile found = null;
		for (Rectangle r : returnObjects) {
			if (!(r instanceof Tile)) {
				continue;
			}
			if (r.getTopLeft().x > playerAreaTemp.getBottomRight().x - 1) {
				continue;
			}
			if (r.getBottomRight().x < playerAreaTemp.getTopLeft().x + 1) {
				continue;
			}
			if (r.getTopLeft().y > playerAreaTemp.getBottomRight().y - 1) {
				continue;
			}
			if (r.getBottomRight().y < playerAreaTemp.getTopLeft().y + 1) {
				continue;
			}
			found = ((Tile) r);
		}
		// if the next block the player will become has been found
		if (found != null) {

			// update the checkpoint
			this.playerCheckpoint = found;

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
			player = new Player(p, box2d, texture, current);
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
					player = new Player(p, box2d, texture, playerCheckpoint);
				} else if (playerStart != null) {
					Tile current = ((PlayerStart) playerStart).getRequired();
					if (current != null) {
						if (this.player != null) {
							this.player.destroy();
						}
						player = new Player(p, box2d, texture, current);
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
	}

	public void draw() {
		pageView.draw();
	}

	public void step(float deltaTime) {
		if (pauseTimer.isFinished()) {
			switch (pauseType) {
			case NONE:
				pauseTimer.stop();
				break;
			case NEXT_LEVEL:
				AppLogic.nextLevel();
				pauseType = PauseType.NONE;
				pauseTimer.stop();
				break;
			case RESTART_LEVEL:
				AppLogic.restartLevel();
				pauseType = PauseType.NONE;
				pauseTimer.stop();
				break;
			case NEXT_PLAYER:
				nextPlayer();
				pauseType = PauseType.NONE;
				pauseTimer.stop();
				break;
			}
		}

		// step player non-physics logic
		if (player != null) {
			player.step(deltaTime);
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

		// update screen space
		PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
		float screenSpaceWidth = convert.screenToLevel(p.width + screenSpaceOffset * 2);
		float screenSpaceHeight = convert.screenToLevel(p.height + screenSpaceOffset * 2);
		screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);

		// step the pause timer
		pauseTimer.deltaStep(deltaTime);

	}

	public void cameraStep(float deltaTime) {
		// step the page view
		pageView.step(deltaTime);
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

	public boolean isPaused() {
		return pauseTimer.isRunning();
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
