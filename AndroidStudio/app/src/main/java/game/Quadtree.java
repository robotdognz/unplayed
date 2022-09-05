package game;

import java.util.HashSet;
import objects.Event;
import objects.Rectangle;
import objects.Tile;
import objects.events.PlayerEnd;
import processing.core.PApplet;

public class Quadtree {
	private Rectangle bounds;
	public QuadNode root;
	private int insertCount = 0;
	private int playerEndCount = 0;

	public Quadtree(Rectangle bounds) {
		this.bounds = bounds;
		root = new QuadNode(bounds, null, this); // top level node has null for parent
	}

	/**
	 * Calculate the world area rectangle that contains all the objects currently in the world.
	 */
	public Rectangle calculateArea() {
		// get all objects
		HashSet<Rectangle> returnObjects = new HashSet<>();
		root.getAll(returnObjects);

		// store coordinates
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;

		// get coordinates
		for (Rectangle r : returnObjects) {
			if (r.getX() < minX) minX = r.getX();
			if (r.getY() < minY) minY = r.getY();
			if (r.getBottomRight().x > maxX) maxX = r.getBottomRight().x;
			if (r.getBottomRight().y > maxY) maxY = r.getBottomRight().y;
		}

		// build and output area rectangle
		Rectangle output = new Rectangle(minX, minY, maxX - minX, maxY - minY);
		return output;
	}

	public HashSet<Rectangle> retrieve(HashSet<Rectangle> returnObjects, Rectangle player) {
		root.retrieve(returnObjects, player);
		return returnObjects;
	}

	public void insert(Rectangle current) {
		insertCount++;
		root.nodeInsert(current);
		if (current instanceof Tile) {
			((Tile) current).create();
		} else if (current instanceof Event) {
			((Event) current).create();
		}

		if (current instanceof PlayerEnd) {
			playerEndCount++;
		}
	}

	public void remove(Rectangle current) {
		insertCount--;
		root.remove(current);
		if (current instanceof Tile) {
			((Tile) current).destroy();
		} else if (current instanceof Event) {
			((Event) current).destroy();
		}

		if (current instanceof PlayerEnd) {
			playerEndCount--;
		}
	}

	public void setRoot(QuadNode newRoot) {
		this.root = newRoot;
	}

	public void draw(PApplet p, float scale) {
		root.draw(p, scale);
	}

	public void clear() {
		insertCount = 0;
		playerEndCount = 0;
		root = new QuadNode(bounds, null, this);
	}

	public int size() {
		HashSet<Rectangle> count = new HashSet<Rectangle>();
		getAll(count);
		return count.size();
	}

	public int insertCount() {
		return insertCount;
	}

	public int playerEndCount() {
		return playerEndCount;
	}

	public HashSet<Rectangle> getAll(HashSet<Rectangle> returnSet) {
		root.getAll(returnSet);
		return returnSet;
	}
}
