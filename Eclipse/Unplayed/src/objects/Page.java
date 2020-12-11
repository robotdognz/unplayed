package objects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import game.Game;
import game.Player;

import java.util.ArrayList;

import processing.core.*;
import static processing.core.PConstants.*;

public class Page extends Editable {
	private PApplet p;
	private Game game;
	private Player player;
	private Rectangle view; // the page's view into the world
	private HashSet<Rectangle> pageObjects;
	private HashSet<String> excludedObjects; // a list of rectangle strings to exclude while drawing

	private PGraphics pageGraphics;
	private boolean redraw = true;
	private PVector position; // center of the page in page view
	Rectangle adjustedRect; // an axis locked rectangle that contains the rotated page (used to check if
							// page is on screen and therefore should be drawn)
	private int snapNo = 15;

	public Page(PApplet p, Game game, PVector topLeft, PVector bottomRight, PVector position) {
		super(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
		this.p = p;
		this.game = game;
		this.player = game.player;
		float rWidth = bottomRight.x - topLeft.x;
		float rHeight = bottomRight.y - topLeft.y;
		this.view = new Rectangle(topLeft.x, topLeft.y, rWidth, rHeight);
		this.pageObjects = new HashSet<Rectangle>();
		this.excludedObjects = new HashSet<String>();

		this.pageGraphics = p.createGraphics((int) rWidth, (int) rHeight, P2D);

		setPosition(position);
	}

	public PVector getPosition() {
		return position;
	}

	public void setPosition(PVector pos) {
		this.position = pos;
		doAdjustedRect();
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

	public void draw(float scale) {

		if (redraw) {
			drawView(scale);
		}
		checkRedraw();

		// draw the page
		p.pushMatrix();
		p.translate(position.x, position.y);
		p.scale(size); // size the page will appear in the page view
		p.rotate(PApplet.radians(Math.round(angle / snapNo) * snapNo)); // angle of the page
		p.scale(flipX, flipY); // flipping the page
		p.imageMode(CENTER);
		p.image(pageGraphics, 0, 0, pageGraphics.width, pageGraphics.height); // draw the page
		p.popMatrix();
	}

	private void drawView(float scale) {
		// draw the view that will be shown on the page
		ArrayList<Rectangle> drawFirst = new ArrayList<Rectangle>();
		ArrayList<Rectangle> drawSecond = new ArrayList<Rectangle>();
		for (Rectangle r : pageObjects) {
			boolean excluded = false;
			for (String s : excludedObjects) { // check the rectangle against the excluded list
				if (r.toString().equals(s)) {
					excluded = true;
				}
			}
			if (!excluded) { // if the rectangle is not on the excluded list
				if (r instanceof Image) {
					drawFirst.add(r);
				} else {
					drawSecond.add(r);
				}
			}
		}

		// one step further would be to draw the player and world behind a
		// pre-rendered page grid (the most expensive part of rendering the page)
		// that only gets redrawn when the LOD changes

		// begin drawing on the page
		pageGraphics.beginDraw();

		pageGraphics.translate(-view.getX(), -view.getY());

		// draw environment and player
		pageGraphics.background(240);

		for (Rectangle r : drawFirst) { // draw images
			if (r instanceof Image) {
				((Image) r).draw(pageGraphics, scale / size);
			}
		}
		for (Rectangle r : drawSecond) { // draw tiles and events
			if (r instanceof Tile) {
				((Tile) r).draw(pageGraphics, scale / size); // scale is divided by size so that LODs are relative
																// to
																// page size
			}
			if (r instanceof Event && ((Event) r).visible) {
				((Event) r).draw(pageGraphics, scale / size);
			}
		}

		game.player.draw(pageGraphics);
		game.paper.draw(pageGraphics, view, scale / size);
		// end drawing on the page
		pageGraphics.endDraw();
	}

	@Override
	public void drawSelected(PGraphics g) {
		g.pushMatrix();
		g.noFill();
		g.stroke(255, 0, 0); // selection color
		g.strokeWeight(2);
		g.translate(position.x, position.y);
		p.scale(size); // size the page will appear in the page view
		g.rotate(PApplet.radians(Math.round(angle / snapNo) * snapNo)); // angle of the page
		g.rectMode(CENTER);
		g.rect(0, 0, adjustedRect.getWidth(), adjustedRect.getHeight());
		g.popMatrix();
	}

	private void checkRedraw() {
		if (view.getTopLeft().x > player.getPlayerArea().getBottomRight().x) {
			redraw = false;
			return;
		}
		if (view.getBottomRight().x < player.getPlayerArea().getTopLeft().x - 20) {
			redraw = false;
			return;
		}
		if (view.getTopLeft().y > player.getPlayerArea().getBottomRight().y + 20) {
			redraw = false;
			return;
		}
		if (view.getBottomRight().y < player.getPlayerArea().getTopLeft().y - 20) {
			redraw = false;
			return;
		}
		redraw = true;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public void addSize(float size) {
		if (this.size + size > 0.5) {
			this.size += size;
		} else {
			this.size = 0.5f;
		}
	}

	public float getSize() {
		return size;
	}

	public void step() {
		// get objects visible to this page
		pageObjects.clear();
		game.world.retrieve(pageObjects, view);

		// the step and draw process could be optimized by getting pageObjects once when
		// the level is run

	}

	public Set<String> getExcluded() {
		return Collections.unmodifiableSet(excludedObjects);
	}

	public Rectangle getView() {
		return view;
	}
}
