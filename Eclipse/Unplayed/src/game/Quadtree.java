package game;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import objects.Rectangle;
import objects.Tile;
import objects.events.CameraChange;
import processing.core.PApplet;

public class Quadtree {
	private Rectangle bounds;
	public QuadNode root;
	private int insertCount = 0;

	private HashSet<CameraChange> cameras; // easily accessible cameras

	public Quadtree(Rectangle bounds) {
		this.bounds = bounds;
		root = new QuadNode(bounds, null, this); // top level node has null for parent
		cameras = new HashSet<CameraChange>();
	}

	public HashSet<Rectangle> retrieve(HashSet<Rectangle> returnObjects, Rectangle player) {
		root.retrieve(returnObjects, player);
		return returnObjects;
	}

	public void insert(Rectangle current) {
		insertCount++;
		if (current instanceof CameraChange) {
			cameras.add((CameraChange) current);
		}
		root.nodeInsert(current);
		if (current instanceof Tile) {
			((Tile) current).create();
		}
	}

	public void remove(Rectangle current) {
		insertCount--;
		if (current instanceof CameraChange) {
			cameras.remove((CameraChange) current);
			root.removeColliders((CameraChange) current);
		}
		root.remove(current);
		if (current instanceof Tile) {
			((Tile) current).destroy();
		}
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
		cameras.clear();
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

	public Set<CameraChange> getCameras() {
		return Collections.unmodifiableSet(cameras);
	}
}
