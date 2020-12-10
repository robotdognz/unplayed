package game;

import java.util.ArrayList;
import java.util.HashSet;

import camera.Camera;
import handlers.TextureCache;
import misc.Converter;
import misc.Vibe;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import processing.core.*;

public class Game {
	private PApplet p;
	public Player player;
	public Paper paper;
	public Converter convert;
	public GameLogic gl;

	public Quadtree world;
	public ArrayList<View> views;
	public Rectangle startingWorld;
	public HashSet<Rectangle> playerObjects;
	private PageView pageView;

	public Camera camera;
	// TODO: move these to editor (need to moved some logic from step too)
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
	public float topEdge;
	public float newTopEdge;
	public float bottomEdge;
	public float newBottomEdge;
	public float leftEdge;
	public float newLeftEdge;
	public float rightEdge;
	public float newRightEdge;
	public float boarderZoomSpeed = 0.1f; // 0.1 is default

	public Game(PApplet p, Camera c, Vibe v, TextureCache texture, Converter convert) {
		// legacy variables from level class TODO: write these out eventually
		PVector playerStart = new PVector(0, 0);
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
		this.camera = c;
		this.convert = convert;

		player = new Player(p, texture, playerStart.x, playerStart.y, v);

		startingWorld = new Rectangle(playerStart.x - 400, playerStart.y - 400, 900, 900);
		world = new Quadtree(startingWorld);
		views = new ArrayList<View>();
		playerObjects = new HashSet<Rectangle>();

		pageView = new PageView(p, camera, convert);

		paper = new Paper(texture);

		// camera
		camera.setScale(startScale);
		newScale = startScale;
		camera.setCenter(startCenter);
		newCenter = new PVector(camera.getCenter().x, camera.getCenter().y);

		// calculate screen space
		screenSpaceOffset = 0; // positive makes it larger, negative makes it smaller
		PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
		float screenSpaceWidth = convert.screenToLevel(p.width + screenSpaceOffset * 2);
		float screenSpaceHeight = convert.screenToLevel(p.height + screenSpaceOffset * 2);
		screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);
		screenObjects = new HashSet<Rectangle>();

		topEdge = bottomOfTopBar;
		newTopEdge = topEdge;
		bottomEdge = topOfBottomBar;
		newBottomEdge = bottomEdge;
		leftEdge = camera.getCenter().x - newScale / 2;
		newLeftEdge = leftEdge;
		rightEdge = camera.getCenter().x + newScale / 2;
		newRightEdge = rightEdge;

		world.insert(new Tile(texture, texture.getTileList().get(0).getFile(), 0, 100));
		// TODO: to be replaced when there is a player start event

		// everything needs to be a multiple of 20 (multiple of 10 so you can always
		// fall down holes, and 20 so you don't clip through things 90 apart because of
		// speed 10)
	}

	public void passGameLogic(GameLogic gl) {
		this.gl = gl;
	}

	public void restart() {
		// legacy variables from level class TODO: write these out eventually
		PVector playerStart = new PVector(0, 0);
		PVector cameraTopLeft = new PVector(-400, -400);
		PVector cameraBottomRight = new PVector(500, 600);
		int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
		int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
		PVector startCenter = new PVector(centerX, centerY);
		int startScale = (int) Math.abs(cameraBottomRight.x - cameraTopLeft.x);
		int bottomOfTopBar = (int) cameraTopLeft.y;
		int topOfBottomBar = (int) cameraBottomRight.y;

		// actual restart code starts here
		player.resetVelocity();
		player.setPosition(playerStart);
		newScale = startScale;
		newCenter = startCenter;
		newTopEdge = bottomOfTopBar;
		newBottomEdge = topOfBottomBar;
		newLeftEdge = newCenter.x - newScale / 2;
		newRightEdge = newCenter.x + newScale / 2;
	}

	public void draw() {
		pageView.draw();

		// TODO: do something with this view border code
//		p.pushMatrix(); // start working at game scale
//		p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen
//
//		// camera
//		p.scale((float) p.width / (float) camera.getScale()); // width/screen fits the level scale to the screen
//		p.scale(camera.getSubScale()); // apply offset for tall screen spaces
//		p.translate(-camera.getCenter().x, -camera.getCenter().y); // moves the view around the level
//
//		// draw black bars
//		if (camera.getGame()) {
//			player.drawArrows(this);
//			p.fill(20, 255); // 10, 255
//			int barSize = 1000000;
//			p.rectMode(CORNERS);
//			// top bar
//			p.rect(-barSize + camera.getCenter().x, camera.getCenter().y - barSize, barSize + camera.getCenter().x,
//					topEdge);
//			// bottom bar
//			p.rect(-barSize + camera.getCenter().x, bottomEdge, barSize + camera.getCenter().x,
//					camera.getCenter().y + barSize);
//			// left bar
//			p.rect(-barSize + camera.getCenter().x, camera.getCenter().y - barSize, leftEdge,
//					camera.getCenter().y + barSize);
//			// right bar
//			p.rect(rightEdge, camera.getCenter().y - barSize, barSize + camera.getCenter().x,
//					camera.getCenter().y + barSize);
//			p.rectMode(CORNER);
//		}
//
//		p.popMatrix(); // start working at screen scale
	}

	public void step() {
		screenObjects.clear();
		world.retrieve(screenObjects, screenSpace);

		// find platforms near the player
		playerObjects.clear();
		world.retrieve(playerObjects, player.getPlayerArea());
		player.step(playerObjects, this);

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
		if (camera.getScale() != newScale || topEdge != newTopEdge || bottomEdge != newBottomEdge) {
			// if there might be a difference in tall screen scale
			if ((newBottomEdge - newTopEdge) / (newRightEdge - newLeftEdge) > (float) p.height / (float) p.width) {
				newSubScale = ((float) p.height / ((float) p.width / (float) (newRightEdge - newLeftEdge)))
						/ (newBottomEdge - newTopEdge);
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
		if (leftEdge != newLeftEdge) {
			leftEdge = PApplet.lerp(leftEdge, newLeftEdge, PApplet.exp(-boarderZoomSpeed));
		}
		if (rightEdge != newRightEdge) {
			rightEdge = PApplet.lerp(rightEdge, newRightEdge, PApplet.exp(-boarderZoomSpeed));
		}
		if (topEdge != newTopEdge) {
			topEdge = PApplet.lerp(topEdge, newTopEdge, PApplet.exp(-boarderZoomSpeed));
		}
		if (bottomEdge != newBottomEdge) {
			bottomEdge = PApplet.lerp(bottomEdge, newBottomEdge, PApplet.exp(-boarderZoomSpeed));
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
		if(views.size() < 1) {
			return null;
		}
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
			//return the first overlap
			return view;
		}
		
		return null;
	}
	
	public void setViews(ArrayList<View> views){
		this.views.clear();
		for(View view : views) {
			this.views.add(view);
		}
	}
}
