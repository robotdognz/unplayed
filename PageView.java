package game;

import java.util.ArrayList;
import java.util.List;
import camera.Camera;
import handlers.TextureCache;
import misc.Converter;
import objects.Background;
import objects.Page;
import objects.Rectangle;
import objects.events.CameraChange;
import processing.core.*;
import static processing.core.PConstants.*;

public class PageView {
	private PApplet p;
	private Game game;
	private Converter convert;
	
	private BackgroundPaper paper;
	
	private ArrayList<Page> pages;
	private ArrayList<Background> backgrounds;

	// world camera
	private Camera camera;

	public PageView(PApplet p, Game game, Camera camera, TextureCache texture, Converter convert) {
		this.p = p;
		this.game = game;
		this.convert = convert;
		this.camera = camera;
		
		this.paper = new BackgroundPaper(texture);
		
		this.pages = new ArrayList<Page>();
		this.backgrounds = new ArrayList<Background>();
	}

	public void draw() {

		p.pushMatrix(); // start working at game scale
		p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen

		// camera
		p.scale((float) p.width / (float) camera.getScale()); // width/screen fits the level scale to the screen
		p.scale(camera.getSubScale()); // apply offset for tall screen spaces
		p.translate(-camera.getCenter().x, -camera.getCenter().y); // moves the view around the level

		float currentScale = convert.getScale();

//		p.background(100); // grey background
		
		// draw the looping background
		paper.draw(p.getGraphics(), game.screenSpace, convert.getScale()); // background paper effect

		// calculate page drawing area
		PVector topLeft;
		PVector bottomRight;
		if (game.camera.getGame()) {
			topLeft = game.cameraArea.getTopLeft();
			bottomRight = game.cameraArea.getBottomRight();
		} else {
			topLeft = convert.screenToLevel(0, 0);
			bottomRight = convert.screenToLevel(p.width, p.height);
		}
		// draw backgrounds that are inside that area
		for (Background background : backgrounds) {
			if (background.leftOf(topLeft.x)) {
				continue;
			}
			if (background.rightOf(bottomRight.x)) {
				continue;
			}
			if (background.above(topLeft.y)) {
				continue;
			}
			if (background.below(bottomRight.y)) {
				continue;
			}

			background.draw(currentScale);
		}
		// draw pages that are inside that area
		for (Page page : pages) {
			if (page.leftOf(topLeft.x)) {
				continue;
			}
			if (page.rightOf(bottomRight.x)) {
				continue;
			}
			if (page.above(topLeft.y)) {
				continue;
			}
			if (page.below(bottomRight.y)) {
				continue;
			}

			page.draw(currentScale);
		}

		// draw existing cameras
		if (!camera.getGame()) {
			for (CameraChange c : game.world.getCameras()) {
				Rectangle area = c.getCameraArea();
				p.noFill();
				p.stroke(c.getColor());
				p.strokeWeight(3);
				p.rectMode(CORNERS);
				p.rect(area.getX(), area.getY(), area.getBottomRight().x, area.getBottomRight().y);
			}
		}

		p.popMatrix();

	}

	public void step() {
		for (Page p : pages) {
			p.step();
		}
	}

	public void forceRedraw() {
		for (Page p : pages) {
			p.createGraphics(); // resize
			p.drawView(); // redraw
		}
	}

	public void addPage(Page page) {
		pages.add(page);
	}

	public void removePage(Page page) {
		pages.remove(page);
	}

	public Page getPage(float x, float y) {
		if (pages.size() < 1) {
			return null;
		}
		for (Page page : pages) {
			// return the first overlap
			if (page.isInside(x, y)) {
				return page;
			}

		}

		return null;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(ArrayList<Page> pages) {
		this.pages = pages;
	}

	public void clearPages() {
		this.pages.clear();
	}
	
	
	
	
	public void addBackground(Background background) {
		backgrounds.add(background);
	}

	public void removeBackground(Background background) {
		backgrounds.remove(background);
	}

	public Background getBackground(float x, float y) {
		if (backgrounds.size() < 1) {
			return null;
		}
		for (Background background : backgrounds) {
			// return the first overlap
			if (background.isInside(x, y)) {
				return background;
			}

		}

		return null;
	}

	public List<Background> getBackgrounds() {
		return backgrounds;
	}

	public void setBackgrounds(ArrayList<Background> backgrounds) {
		this.backgrounds = backgrounds;
	}

	public void clearBackgrounds() {
		this.backgrounds.clear();
	}
}
