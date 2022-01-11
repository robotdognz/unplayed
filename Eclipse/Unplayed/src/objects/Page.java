package objects;

//import java.util.Collections;
import java.util.HashSet;
//import java.util.Set;

import editor.Editor;
import game.Game;

import processing.core.*;
import static processing.core.PConstants.*;

public class Page extends Editable {
	private PApplet p;
	private Game game;
	private View view;
	private HashSet<Rectangle> pageObjects;
	// private HashSet<String> excludedObjects; // a list of rectangle strings to
	// exclude while drawing

	private PGraphics pageGraphics;
	private PGraphics tiles;
	private int shadowOffset; // the absolute amount to offset the shadow by
	private int shadow; // the relative amount to offset the shadow by
	private boolean redraw = true;
	private PVector position; // center of the page in page view

	// Fields used for updating contents of page
	private int worldCount = -1;
	private int placedCount = -1;
	private int removedCount = -1;

	// Page corners relative to center, used to check if the page is on screen
	PVector topLeft;
	PVector topRight;
	PVector bottomLeft;
	PVector bottomRight;

	// exclusion booleans
	public boolean showPlayer;
	public boolean showObstacles;
	public boolean showTiles;
	public boolean showImages;

	// player visibility
	private boolean playerVisible;
	private boolean playerVisibleChanged;

	public Page(PApplet p, Game game, View view, PVector position) { // PVector topLeft, PVector bottomRight,
		super(view.getTopLeft().x, view.getTopLeft().y, view.getWidth(), view.getHeight());
		this.p = p;
		this.game = game;
		this.view = view;
		this.pageObjects = new HashSet<Rectangle>();
		// this.excludedObjects = new HashSet<String>();

		// booleans
		showPlayer = true;
		showObstacles = true;
		showTiles = true;
		showImages = true;

		this.shadowOffset = 9;
		this.shadow = 9;

		setPosition(position);
		createGraphics();
	}

	public void createGraphics() {
		if (this.pageGraphics == null || this.pageGraphics.width != view.getWidth()
				|| this.pageGraphics.height != view.getHeight()) {

			this.pageGraphics = p.createGraphics((int) view.getWidth(), (int) view.getHeight(), P2D);
			this.tiles = p.createGraphics((int) view.getWidth(), (int) view.getHeight(), P2D);

			setWidth(view.getWidth());
			setHeight(view.getHeight());

			updateCorners();
		}
	}

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
		updateCorners();
	}

	public void addPosition(float x, float y) {
		this.position.x += x;
		this.position.y += y;
		updateCorners();
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

		updatePlayerVisibility();
	}

	private void updatePlayerVisibility() {
		// update player visibility
		if (game.player != null) {
			boolean temp = false;
			while (temp == false) {
//				if (game.player.getCenter().x - game.player.getWidth() * 0.5 > view.getBottomRight().x - 1) {
//					break;
//				}
//				if (game.player.getCenter().x + game.player.getWidth() * 0.5 < view.getTopLeft().x + 1) {
//					break;
//				}
//				if (game.player.getCenter().y - game.player.getWidth() * 0.5 > view.getBottomRight().y - 1) {
//					break;
//				}
//				if (game.player.getCenter().y + game.player.getWidth() * 0.5 < view.getTopLeft().y + 1) {
//					break;
//				}
				if (game.player.getCenter().x > view.getBottomRight().x) {
					break;
				}
				if (game.player.getCenter().x < view.getTopLeft().x) {
					break;
				}
				if (game.player.getCenter().y > view.getBottomRight().y) {
					break;
				}
				if (game.player.getCenter().y < view.getTopLeft().y) {
					break;
				}

				temp = true;
			}

			if (temp != playerVisible) {
				playerVisible = temp;
				playerVisibleChanged = true;
			}

		} else {
			playerVisible = false;
		}
	}

	public boolean playerVisibilityChanged() {
		// has player visibility changed since last check?
		boolean temp = playerVisibleChanged;
		playerVisibleChanged = false;
		return temp;

	}

//	public void exclude(Rectangle object) {
//		excludedObjects.add(object.toString());
//	}

	public void draw(float scale) {
		drawNew(scale);
//		drawOld(scale);
	}

	private void drawNew(float scale) {
		// draw the page
		p.pushMatrix();
		p.translate(position.x, position.y);
		p.scale(size); // size the page will appear in the page view
		p.rotate(PApplet.radians(angle)); // rotate the page

		// draw the shadow
		p.translate(shadow, shadow);
		p.fill(0, 40);
		p.noStroke();
		p.rectMode(CENTER);
		p.rect(0, 0, view.getWidth(), view.getHeight());
		p.translate(-shadow, -shadow);

		// draw the page itself
		p.scale(flipX, flipY); // flip the page
		p.imageMode(CENTER);

		// draw the page background
		p.fill(240);
		p.rectMode(CORNER);
		p.rect(view.getWidth() / 2, view.getHeight() / 2, view.getWidth(), view.getHeight());

		// draw tiles and images
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
			if (r instanceof Image && showImages) {
				((Image) r).draw(p.g, 3); // scale/size
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
			if (r instanceof Tile && showTiles) {
				((Tile) r).draw(p.g, 3); // scale/size
			}
			if (r instanceof Event && ((Event) r).visible && showObstacles) {
				((Event) r).draw(p.g, 3); // scale/size
			}
		}

		// draw player and paper effect
		if (game.player != null && showPlayer) {
			game.player.draw(p.g, 3); // player scale/size
		}
		game.paper.draw(p.g, view, scale / size); // paper effect

		p.popMatrix();
	}

	private void drawOld(float scale) {
		if (redraw) {
			drawView();
			redraw = false;
		}

		pageGraphics.beginDraw();
		pageGraphics.background(240); // background
		pageGraphics.imageMode(CORNER);
		pageGraphics.image(tiles, 0, 0); // environment
		pageGraphics.translate(-view.getX(), -view.getY());
		if (game.player != null && showPlayer) {
			game.player.draw(pageGraphics, 3); // player scale/size
		}
		game.paper.draw(pageGraphics, view, scale / size); // paper effect
		pageGraphics.endDraw();

		// draw the page
		p.pushMatrix();
		p.translate(position.x, position.y);
		p.scale(size); // size the page will appear in the page view
		p.rotate(PApplet.radians(angle)); // rotate the page

		// draw the shadow
		p.translate(shadow, shadow);
		p.fill(0, 40);
		p.noStroke();
		p.rectMode(CENTER);
		p.rect(0, 0, pageGraphics.width, pageGraphics.height);
		p.translate(-shadow, -shadow);

		// draw the page itself
		p.scale(flipX, flipY); // flip the page
		p.imageMode(CENTER);
		p.image(pageGraphics, 0, 0); // draw the page
		p.popMatrix();
	}

	public void drawCorners() {
		// draw page corners
		if (Editor.autoCameraSearch && playerVisible) {
			p.rectMode(CENTER);
			p.fill(255, 0, 0);
			p.rect(topLeft.x, topLeft.y, 10, 10);
			p.rect(topRight.x, topRight.y, 10, 10);
			p.rect(bottomLeft.x, bottomLeft.y, 10, 10);
			p.rect(bottomRight.x, bottomRight.y, 10, 10);
		}
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
			if (r instanceof Image && showImages) {
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
			if (r instanceof Tile && showTiles) {
				((Tile) r).draw(tiles, 3); // scale/size
			}
			if (r instanceof Event && ((Event) r).visible && showObstacles) {
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
		g.rect(0, 0, getWidth(), getHeight());
		g.popMatrix();
	}

	public boolean playerVisible() {
		return playerVisible;
	}

	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
		updateCorners();
	}

	@Override
	public void addAngle(float angle) {
		super.addAngle(angle);
		updateCorners();
	}

	public void setSize(float size) {
		this.size = size;
		updateCorners();
		updateShadow();
	}

	public void addSize(float size) {
		if (this.size + size > 0.5) {
			this.size += size;
		} else {
			this.size = 0.5f;
		}
		updateCorners();
		updateShadow();
	}

	public float getSize() {
		return size;
	}

//	public Set<String> getExcluded() {
//		return Collections.unmodifiableSet(excludedObjects);
//	}

	public View getView() {
		return view;
	}

	private void updateShadow() {
		this.shadow = (int) (shadowOffset / size);
	}

	// --------------update the corner PVectors---------------
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

	// ------------is a point inside the page-------------
	public boolean isInside(float x, float y) {
		PVector point = new PVector(x, y);
		point.x -= position.x;
		point.y -= position.y;
		point.rotate(PApplet.radians(-angle));

		if (-(getWidth() / 2) * size > point.x) {
			return false;
		}
		if ((getWidth() / 2) * size < point.x) {
			return false;
		}
		if (-(getHeight() / 2) * size > point.y) {
			return false;
		}
		if ((getHeight() / 2) * size < point.y) {
			return false;
		}

		return true;
	}

	// get the bounding box edges for the page
	public float getLeftmostPoint() {
		return Math.min(Math.min(topLeft.x, topRight.x), Math.min(bottomLeft.x, bottomRight.x));
	}

	public float getRightmostPoint() {
		return Math.max(Math.max(topLeft.x, topRight.x), Math.max(bottomLeft.x, bottomRight.x));
	}

	public float getTopmostPoint() {
		return Math.min(Math.min(topLeft.y, topRight.y), Math.min(bottomLeft.y, bottomRight.y));
	}

	public float getBottommostPoint() {
		return Math.max(Math.max(topLeft.y, topRight.y), Math.max(bottomLeft.y, bottomRight.y));
	}

	// ----------is this page off camera------------

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

	public boolean above(float y) {
		if (topLeft.y > y) {
			return false;
		}
		if (topRight.y > y) {
			return false;
		}
		if (bottomLeft.y > y) {
			return false;
		}
		if (bottomRight.y > y) {
			return false;
		}
		return true;
	}

	public boolean below(float y) {
		if (topLeft.y < y) {
			return false;
		}
		if (topRight.y < y) {
			return false;
		}
		if (bottomLeft.y < y) {
			return false;
		}
		if (bottomRight.y < y) {
			return false;
		}
		return true;
	}
}
