package game;

import java.util.HashSet;

import objects.Rectangle;
import processing.core.PApplet;

public class Quadtree {
	private Rectangle bounds;
	public QuadNode root;
	private int insertCount = 0;

	public Quadtree(Rectangle bounds) {
		this.bounds = bounds;
		root = new QuadNode(bounds, null, this); // top level node has null for parent
	}

	public HashSet<Rectangle> retrieve(HashSet<Rectangle> returnObjects, Rectangle player) {
		root.retrieve(returnObjects, player);
		return returnObjects;
	}

	public void insert(Rectangle current) {
		insertCount++;
		root.nodeInsert(current);
	}

	public void remove(Rectangle current) {
		insertCount--;
		root.remove(current);
	}

	public void setRoot(QuadNode newRoot) {
		this.root = newRoot;
	}

	public void draw(PApplet p) {
		root.draw(p);
	}

	public void clear() {
		insertCount = 0;
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

	public HashSet<Rectangle> getAll(HashSet<Rectangle> returnSet) {
		root.getAll(returnSet);
		return returnSet;
	}
}
