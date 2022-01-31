package game;

import java.util.ArrayList;
import java.util.List;
import camera.Camera;
import camera.PageViewCamera;
import editor.DebugOutput;
import editor.Editor;
import handlers.TextureCache;
import misc.Converter;
import objects.Background;
import objects.Page;
import processing.core.*;
import ui.Menu;

public class PageView {
	private PApplet p;
	private Converter convert;

	private BackgroundPaper paper;

	private ArrayList<Page> pages;
	private ArrayList<Background> backgrounds;

	private PageViewCamera pageCamera;

	public PageView(PApplet p, Game game, TextureCache texture, Converter convert) {
		this.p = p;
		this.convert = convert;

		this.pageCamera = new PageViewCamera(p);

		this.paper = new BackgroundPaper(p, texture);

		this.pages = new ArrayList<Page>();
		this.backgrounds = new ArrayList<Background>();
	}

	public void draw() {

		p.pushMatrix(); // start working at game scale
		p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen

		float currentScale;
		PVector topLeft;
		PVector bottomRight;

		if (Camera.getGame()) {
			// auto camera
			p.scale((float) p.width / (float) pageCamera.getScale()); // width/screen fits the level scale to the screen
			p.scale(pageCamera.getSubScale()); // apply offset for tall screen spaces
			p.translate(-pageCamera.getCenter().x, -pageCamera.getCenter().y); // moves the view around the level

			currentScale = pageCamera.getScale() / pageCamera.getSubScale() / 100;
			topLeft = convert.screenToLevel(0, 0, pageCamera.getScale(), pageCamera.getSubScale(),
					pageCamera.getCenter());
			bottomRight = convert.screenToLevel(p.width, p.height, pageCamera.getScale(), pageCamera.getSubScale(),
					pageCamera.getCenter());

		} else {
			// editor camera
			p.scale((float) p.width / (float) Camera.getScale()); // width/screen fits the level scale to the screen
			p.scale(Camera.getSubScale()); // apply offset for tall screen spaces
			p.translate(-Camera.getCenter().x, -Camera.getCenter().y); // moves the view around the level

			currentScale = convert.getScale();
			topLeft = convert.screenToLevel(0, 0);
			bottomRight = convert.screenToLevel(p.width, p.height);
		}

		// draw the looping background
//		p.background(217, 201, 170);
		paper.draw(p.getGraphics(), topLeft, bottomRight, currentScale); // background paper effect

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

		if (AppLogic.hasMenu()) {
			AppLogic.getMenu().drawPageView();
		}

		// draw auto generated camera
		if (Editor.autoCameraSearch && !Camera.getGame()) {
			pageCamera.draw();
		}

		p.popMatrix();

	}

	public void step(float deltaTime) {
		boolean adjustCamera = false;

		if (AppLogic.menuAdded() == true) {
			Menu menu = AppLogic.getMenu();

			float minX = menu.getLeftmostPoint();
			float minY = menu.getTopmostPoint();
			float maxX = menu.getRightmostPoint();
			float maxY = menu.getBottommostPoint();

			pageCamera.update(minX, minY, maxX, maxY);
			DebugOutput.pushMessage("Menu added", 1);

		} else if (AppLogic.menuRemoved() == true) {
			adjustCamera = true;
		} else {

			for (Page page : pages) {
				page.step();
				if (page.playerVisibilityChanged()) {
					adjustCamera = true;
				}
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

	public void initCamera() {
		for (Page page : pages) {
			page.step();
			page.updateSizeFromView(); // recaulculate page corners
		}
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
			pageCamera.initCamera(minX, minY, maxX, maxY);
		}

	}

	public void forceRedraw() {
		// TODO: either remove this method, or repurpose it
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
