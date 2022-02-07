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
import objects.Rectangle;
import processing.core.*;
import ui.Menu;

public class PageView {
	private PApplet p;
	private Converter convert;

	private BackgroundPaper paper;

	private ArrayList<Page> pages;
	private ArrayList<Background> backgrounds;

	private PageViewCamera pageCamera;

	public PageViewCamera getPageCamera() {
		return pageCamera;
	}

	private Menu storedMenu;
	private boolean removeMenu = false;

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

		// draw current menu, destroy it if it's off camera
		if (storedMenu != null) {
			storedMenu.drawPageView(currentScale);

			if (removeMenu == true && (storedMenu.leftOf(pageCamera.getCameraArea().getTopLeft().x)
					|| storedMenu.rightOf(pageCamera.getCameraArea().getBottomRight().x)
					|| storedMenu.above(pageCamera.getCameraArea().getTopLeft().y)
					|| storedMenu.below(pageCamera.getCameraArea().getBottomRight().y))) {
				removeMenu = false;
				storedMenu = null;
			}
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
			storedMenu = AppLogic.getMenu();

			if (!storedMenu.isBuilt()) {
				// build the page menu if it isn't already built
				if (pages.size() > 0) {
					float minX = Float.POSITIVE_INFINITY;
					float minY = Float.POSITIVE_INFINITY;
					float maxX = Float.NEGATIVE_INFINITY;
					float maxY = Float.NEGATIVE_INFINITY;
					for (Page page : pages) {

						minX = Math.min(minX, page.getLeftmostPoint());
						minY = Math.min(minY, page.getTopmostPoint());
						maxX = Math.max(maxX, page.getRightmostPoint());
						maxY = Math.max(maxY, page.getBottommostPoint());

					}
					Rectangle pageArea = new Rectangle(minX, minY, maxX - minX, maxY - minY);
					storedMenu.buldPageMenu(pageCamera.getCenter(), pageArea, pageCamera);
				} else {
					if (storedMenu.child == null) {
						storedMenu.buldPageMenu();
					} else {
						Rectangle area = getFullArea();
						storedMenu.buldPageMenu(pageCamera.getCenter(), area, pageCamera);
					}
				}
			}

			removeMenu = false;

			float menuMinX = storedMenu.getLeftmostPoint();
			float menuMinY = storedMenu.getTopmostPoint();
			float menuMaxX = storedMenu.getRightmostPoint();
			float menuMaxY = storedMenu.getBottommostPoint();
			pageCamera.updateMenu(menuMinX, menuMinY, menuMaxX, menuMaxY);

			DebugOutput.pushMessage("Page menu built", 1);

		} else if (AppLogic.menuRemoved() == true) {
			removeMenu = true;
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
			Rectangle area = getPlayerVisibleArea();
			if (area != null) {
				pageCamera.update(area.getTopLeft().x, area.getTopLeft().y, area.getBottomRight().x,
						area.getBottomRight().y);
			}
		}

		// move the camera
		boolean temp = pageCamera.step(deltaTime);
		// if the camera has finished moving
		if (!temp && storedMenu != null) {
			storedMenu.activate();
		}

	}

	public void stepPages() {
		// used to run the pages so that visibility information etc can be accurately
		// assessed, this is done by the level loading system
		for (Page page : pages) {
			page.step();
		}
	}

	public Rectangle getPlayerVisibleArea() {
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

		if (visiblePage > 0) {
			Rectangle output = new Rectangle(minX, minY, maxX - minX, maxY - minY);
			return output;
		} else {
			return null;
		}
	}

	public void initCamera() {
		if (AppLogic.hasMenu()) {
			// initialize camera for menu
			storedMenu = AppLogic.getMenu();
			storedMenu.buldPageMenu();

			removeMenu = false;

			float menuMinX = storedMenu.getLeftmostPoint();
			float menuMinY = storedMenu.getTopmostPoint();
			float menuMaxX = storedMenu.getRightmostPoint();
			float menuMaxY = storedMenu.getBottommostPoint();
			pageCamera.initCamera(menuMinX, menuMinY, menuMaxX, menuMaxY);
		}
	}

	public Rectangle getFullArea() {
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;

		// get area of pages
		for (Page page : pages) {
			minX = Math.min(minX, page.getLeftmostPoint());
			minY = Math.min(minY, page.getTopmostPoint());
			maxX = Math.max(maxX, page.getRightmostPoint());
			maxY = Math.max(maxY, page.getBottommostPoint());
		}

		Menu temp = storedMenu;
		while (temp != null) {
			if (temp.isBuilt()) {
				minX = Math.min(minX, storedMenu.getLeftmostPoint());
				minY = Math.min(minY, storedMenu.getTopmostPoint());
				maxX = Math.max(maxX, storedMenu.getRightmostPoint());
				maxY = Math.max(maxY, storedMenu.getBottommostPoint());
			}
			temp = temp.child; // update current
		}

		float x = minX;
		float y = minY;
		float width = maxX - minX;
		float height = maxY - minY;

		return new Rectangle(x, y, width, height);
	}

	public Rectangle getPageArea() {
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;

		// get area of pages
		for (Page page : pages) {
			minX = Math.min(minX, page.getLeftmostPoint());
			minY = Math.min(minY, page.getTopmostPoint());
			maxX = Math.max(maxX, page.getRightmostPoint());
			maxY = Math.max(maxY, page.getBottommostPoint());
		}

		float x = minX;
		float y = minY;
		float width = maxX - minX;
		float height = maxY - minY;

		return new Rectangle(x, y, width, height);
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

	public void clearMenus() {
		storedMenu = null;
		removeMenu = false;
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

	public void offsetAll(float x, float y) {
		for (Page p : pages) {
			PVector pos = p.getPosition();
			pos.x += x;
			pos.y += y;
			p.setPosition(pos);
		}
		for (Background b : backgrounds) {
			PVector pos = b.getPosition();
			pos.x += x;
			pos.y += y;
			b.setPosition(pos);
		}
	}
}
