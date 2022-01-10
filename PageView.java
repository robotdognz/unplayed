package game;

import java.util.ArrayList;
import java.util.List;
import camera.Camera;
import camera.PageViewCamera;
import editor.Editor;
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


	private PageViewCamera pageCamera;

	public PageView(PApplet p, Game game, TextureCache texture, Converter convert) {
		this.p = p;
		this.game = game;
		this.convert = convert;

		this.pageCamera = new PageViewCamera(p);

		this.paper = new BackgroundPaper(texture);

		this.pages = new ArrayList<Page>();
		this.backgrounds = new ArrayList<Background>();
	}

	public void draw() {

		p.pushMatrix(); // start working at game scale
		p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen

		// camera
		p.scale((float) p.width / (float) Camera.getScale()); // width/screen fits the level scale to the screen
		p.scale(Camera.getSubScale()); // apply offset for tall screen spaces
		p.translate(-Camera.getCenter().x, -Camera.getCenter().y); // moves the view around the level

		float currentScale = convert.getScale();

		// draw the looping background
		paper.draw(p.getGraphics(), game.screenSpace, convert.getScale()); // background paper effect

		// calculate page drawing area
		PVector topLeft;
		PVector bottomRight;
		if (Camera.getGame()) {
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
			if (Editor.autoCameraSearch && !Camera.getGame()) {
				page.drawCorners();
			}
		}

		// draw auto generated camera
		if (Editor.autoCameraSearch && !Camera.getGame()) {
//			p.noFill();
//			p.stroke(255, 0, 0);
//			p.strokeWeight(3);
//			p.rectMode(CORNERS);
//			p.rect(pageCamera.getTopLeft().x, pageCamera.getTopLeft().y, pageCamera.getBottomRight().x,
//					pageCamera.getBottomRight().y);
			pageCamera.draw();
		}

		// draw existing cameras
		if (!Camera.getGame()) {
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

	public void step(float deltaTime) {
		boolean adjustCamera = false;
		for (Page page : pages) {
			page.step();
			if (page.playerVisibilityChanged()) {
				adjustCamera = true;
			}
		}

		if (adjustCamera) {
			// update the camera zone
			float minX = Float.POSITIVE_INFINITY;
			float minY = Float.POSITIVE_INFINITY;
			float maxX = Float.NEGATIVE_INFINITY;
			float maxY = Float.NEGATIVE_INFINITY;
			int visiblePage = 0;
			for (Page page : pages) {
				if (page.playerVisible()) {
					// if this page has a visible player
					visiblePage++;
					minX = Math.min(minX, page.getLeftmostPoint());
					minY = Math.min(minY, page.getTopmostPoint());
					maxX = Math.max(maxX, page.getRightmostPoint());
					maxY = Math.max(maxY, page.getBottommostPoint());
				}
			}

			// only update camera if player is visible somewhere
			if (visiblePage > 0) {
				pageCamera.update(minX, minY, maxX, maxY);
			}
		}

		pageCamera.step(deltaTime);
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
