package objects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import game.Game;

import processing.core.*;
import static processing.core.PConstants.*;

public class Page extends Editable {
	private PApplet p;
	private Game game;
	private Rectangle view; // the page's view into the world
	private HashSet<Rectangle> pageObjects;
	private HashSet<String> excludedObjects; // a list of rectangle strings to exclude while drawing

	private PGraphics pageGraphics;
	private PGraphics tiles;
	private boolean redraw = true;
	private PVector position; // center of the page in page view
	Rectangle adjustedRect; // an axis locked rectangle that contains the rotated page (used to check if
							// page is on screen and therefore should be drawn)

//	private PShape border;

	// Fields used for updating contents of page
	private int worldCount = -1;
	private int placedCount = -1;
	private int removedCount = -1;

	// Page corners, relative to center
	PVector topLeft;
	PVector topRight;
	PVector bottomLeft;
	PVector bottomRight;

	public Page(PApplet p, Game game, PVector topLeft, PVector bottomRight, PVector position) {
		super(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
		this.p = p;
		this.game = game;
		float rWidth = bottomRight.x - topLeft.x;
		float rHeight = bottomRight.y - topLeft.y;
		this.view = new Rectangle(topLeft.x, topLeft.y, rWidth, rHeight);
		this.pageObjects = new HashSet<Rectangle>();
		this.excludedObjects = new HashSet<String>();

		this.pageGraphics = p.createGraphics((int) rWidth, (int) rHeight, P2D);
		this.tiles = p.createGraphics((int) rWidth, (int) rHeight, P2D);

		setPosition(position);
//		makeBorder();
		updateCorners();
	}

	private void updateCorners() {
		if (topLeft == null) {
			// Initialize
			topLeft = new PVector();
			topRight = new PVector();
			bottomLeft = new PVector();
			bottomRight = new PVector();
		}
		// set values
		topLeft.x = 0 - (getWidth() / 2) * size;
		topLeft.y = 0 - (getHeight() / 2) * size;
		topRight.x = 0 + (getWidth() / 2) * size;
		topRight.y = 0 - (getHeight() / 2) * size;
		bottomLeft.x = 0 - (getWidth() / 2) * size;
		bottomLeft.y = 0 + (getHeight() / 2) * size;
		bottomRight.x = 0 + (getWidth() / 2) * size;
		bottomRight.y = 0 + (getHeight() / 2) * size;
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

	public boolean isInside(float x, float y) {
		return false;
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

//	private void makeBorder() {
//		// used for selecting the page
//		border = p.createShape(RECT, 0 - (getWidth() / 2), 0 - (getHeight() / 2), getWidth(), getHeight());
////		border.setStroke(p.color(0,255,0));
////		border.noFill();
////		border.setStrokeWeight(4);
//		border.scale(size);
//		border.rotate(PApplet.radians(angle));
//		border.translate(position.x, position.y);
//
//		// TODO: try using this to make a bounding box
//		// draw the width and height on screen and see if they correspond to the sides
//		// of the box, or its bounding box
//		// if it's the width and height of the bounding box, then use that
//	}

	public PVector getPosition() {
		return position;
	}

	public void setPosition(PVector pos) {
		if (position == null) {
			this.position = pos;
		} else {
			this.position.x = pos.x;
			this.position.y = pos.y;
		}
		doAdjustedRect();
//		makeBorder();
		updateCorners();
	}

	public void addPosition(float x, float y) {
		this.position.x += x;
		this.position.y += y;
		doAdjustedRect();
//		makeBorder();
		updateCorners();
	}

	private void doAdjustedRect() {
		// TODO: needs to take into account rotation and size
		if (adjustedRect == null) {
			adjustedRect = new Rectangle(position.x - getWidth() / 2, position.y - getHeight() / 2, getWidth(),
					getHeight());
		} else {
			adjustedRect.setX(position.x - getWidth() / 2);
			adjustedRect.setY(position.y - getHeight() / 2);
		}
	}

	public void exclude(Rectangle object) {
		excludedObjects.add(object.toString());
	}

	public Rectangle getAdjusted() {
		return adjustedRect;
	}

//	public PShape getBorder() {
//		return border;
//	}

	public void draw(float scale) {
		if (redraw) {
			drawView();
			redraw = false;
		}

		pageGraphics.beginDraw();
		pageGraphics.background(240); // background
		pageGraphics.imageMode(CORNER);
		pageGraphics.image(tiles, 0, 0); // environment
		pageGraphics.translate(-view.getX(), -view.getY());
		if (game.player != null) {
			game.player.draw(pageGraphics, 3); // player scale/size
		}
		game.paper.draw(pageGraphics, view, scale / size); // paper effect
		pageGraphics.endDraw();

		// draw the page
		p.pushMatrix();
		p.translate(position.x, position.y);
		p.scale(size); // size the page will appear in the page view
		p.rotate(PApplet.radians(angle)); // rotate the page
		p.scale(flipX, flipY); // flip the page
		p.imageMode(CENTER);
		p.image(pageGraphics, 0, 0); // draw the page
		p.popMatrix();

		p.rectMode(CENTER);
		p.fill(255, 0, 0);
		p.rect(topLeft.x, topLeft.y, 10, 10);
		p.rect(topRight.x, topRight.y, 10, 10);
		p.rect(bottomLeft.x, bottomLeft.y, 10, 10);
		p.rect(bottomRight.x, bottomRight.y, 10, 10);
	}

	public void step() {
		// get objects visible to this page
		pageObjects.clear();
		game.world.retrieve(pageObjects, view);

		// check if anything in the page has changed
		if (pageObjects.size() != worldCount || game.placed.size() != placedCount
				|| game.removed.size() != removedCount) {
			redraw = true;
		}

		// update previous count fields
		worldCount = pageObjects.size();
		placedCount = game.placed.size();
		removedCount = game.removed.size();

	}

	public void drawView() { // float scale

		tiles.beginDraw();
		tiles.background(0, 0); // background
		tiles.translate(-view.getX(), -view.getY());

		for (Rectangle r : pageObjects) { // draw images
			if (!(r instanceof Image)) {
				continue;
			}
			if (r.getTopLeft().x > view.getBottomRight().x - 1) {
				continue;
			}
			if (r.getBottomRight().x < view.getTopLeft().x + 1) {
				continue;
			}
			if (r.getTopLeft().y > view.getBottomRight().y - 1) {
				continue;
			}
			if (r.getBottomRight().y < view.getTopLeft().y + 1) {
				continue;
			}
			if (r instanceof Image) {
				((Image) r).draw(tiles, 3); // scale/size
			}
		}

		for (Rectangle r : pageObjects) { // draw tiles and events
			if (!(r instanceof Tile || r instanceof Event)) {
				continue;
			}
			if (r.getTopLeft().x > view.getBottomRight().x - 1) {
				continue;
			}
			if (r.getBottomRight().x < view.getTopLeft().x + 1) {
				continue;
			}
			if (r.getTopLeft().y > view.getBottomRight().y - 1) {
				continue;
			}
			if (r.getBottomRight().y < view.getTopLeft().y + 1) {
				continue;
			}
			if (r instanceof Tile) {
				((Tile) r).draw(tiles, 3); // scale/size
			}
			if (r instanceof Event && ((Event) r).visible) {
				((Event) r).draw(tiles, 3); // scale/size
			}
		}

		tiles.endDraw();
	}

	@Override
	public void drawSelected(PGraphics g) {
		g.pushMatrix();
		g.noFill();
		g.stroke(255, 0, 0); // selection color
		g.strokeWeight(2);
		g.translate(position.x, position.y);
		g.scale(size); // size the page will appear in the page view
		g.rotate(PApplet.radians(angle)); // angle of the page
		g.rectMode(CENTER);
		g.rect(0, 0, adjustedRect.getWidth(), adjustedRect.getHeight());
		g.popMatrix();
	}

	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
//		makeBorder();
		updateCorners();
	}

	@Override
	public void addAngle(float angle) {
		super.addAngle(angle);
//		makeBorder();
		updateCorners();
	}

	public void setSize(float size) {
		this.size = size;
//		makeBorder();
		updateCorners();
	}

	public void addSize(float size) {
		if (this.size + size > 0.5) {
			this.size += size;
		} else {
			this.size = 0.5f;
		}
//		makeBorder();
		updateCorners();
	}

	public float getSize() {
		return size;
	}

	public Set<String> getExcluded() {
		return Collections.unmodifiableSet(excludedObjects);
	}

	public Rectangle getView() {
		return view;
	}
}
