package objects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import game.Game;

import java.util.ArrayList;

import processing.core.*;
import static processing.core.PConstants.*;

public class Page extends Editable {
	private PApplet p;
	private Game game;
	private Rectangle view; // the page's view into the world
	private HashSet<Rectangle> pageObjects;
	private HashSet<String> excludedObjects; // a list of rectangle strings to exclude while drawing

	private PGraphics pageGraphics;
	private PVector position; // center of the page in page view
	Rectangle adjustedRect; // an axis locked rectangle that contains the rotated page (used to check if
							// page is on screen and therefore should be drawn)

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
		setPosition(position);
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

		// if(player is inside the page){ TODO: implement this
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
				((Tile) r).draw(pageGraphics, scale / size); // scale is divided by size so that LODs are relative to
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
		// }

		// draw the page
		p.imageMode(CENTER);
		p.pushMatrix();
		p.translate(position.x, position.y);

		p.scale(size); // size the page will appear in the page view
		p.rotate(PApplet.radians(angle)); // angle of the page
		p.scale(flipX, flipY); // flipping the page
		p.image(pageGraphics, 0, 0, pageGraphics.width, pageGraphics.height); // draw the page
		p.popMatrix();
	}
	
	@Override
	public void drawSelected(PGraphics g) {
		g.noFill();
		g.stroke(255, 0, 0); // selection color
		g.strokeWeight(2);
		g.rectMode(CORNER);
		g.rect(adjustedRect.getX(), adjustedRect.getY(), adjustedRect.getWidth(), adjustedRect.getHeight());
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
