package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camera.Camera;
import handlers.TextureCache;
import misc.Converter;
import objects.Page;
import objects.Rectangle;
import objects.events.CameraChange;
import processing.core.*;
import static processing.core.PConstants.*;

public class PageView {
	private PApplet p;
	private Game game;
	private TextureCache texture;
	private Converter convert;
	private ArrayList<Page> pages;

	// world camera
	private Camera camera;

	public PageView(PApplet p, Game game, Camera camera, TextureCache texture, Converter convert) {
		this.p = p;
		this.game = game;
		this.texture = texture;
		this.convert = convert;
		this.camera = camera;
		pages = new ArrayList<Page>();
	}

	public void draw() {

		p.pushMatrix(); // start working at game scale
		p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen

		// camera
		p.scale((float) p.width / (float) camera.getScale()); // width/screen fits the level scale to the screen
		p.scale(camera.getSubScale()); // apply offset for tall screen spaces
		p.translate(-camera.getCenter().x, -camera.getCenter().y); // moves the view around the level

		float currentScale = convert.getScale();

		p.background(100);
		// draw the desk
		p.imageMode(CENTER);
		PImage temp = texture.getDeskBehind();
		float ratio = (float) temp.height / (float) temp.width;
		p.image(temp, 0, 0, p.width * 5, p.width * 5 * ratio);

		for (Page page : pages) {
			
//			PShape border = page.getBorder();
//			PVector pageTopLeft = border.getVertex(0);
//			PVector pageTopRight = border.getVertex(1);
//			PVector pageBottomRight = border.getVertex(2);
//			PVector pageBottomLeft = border.getVertex(3);
			PVector screenTopLeft = convert.screenToLevel(0, 0);
//			PVector screenTopRight = convert.screenToLevel(p.width, 0);
//			PVector screenBottomRight = convert.screenToLevel(p.width, p.height);
//			PVector screenBottomLeft = convert.screenToLevel(0, p.height);
//			if(pageTopLeft.x < screenTopLeft.x 
//					&& pageTopRight.x < screenTopLeft.x
//					&& pageBottomRight.x < screenTopLeft.x
//					&& pageBottomLeft.x < screenTopLeft.x) {
//				continue;
//			}
			if(page.leftOf(screenTopLeft.x-20)) {
				continue;
			}
			
			page.draw(currentScale);
		}

		// draw desk shading
		p.imageMode(CENTER);
		p.image(texture.getDeskInfront(), 0, 0, p.width * 5, p.width * 5 * ratio);

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
			Rectangle p = page.getAdjusted();
			if (p.getTopLeft().x > x) {
				continue;
			}
			if (p.getBottomRight().x < x) {
				continue;
			}
			if (p.getTopLeft().y > y) {
				continue;
			}
			if (p.getBottomRight().y < y) {
				continue;
			}
			// return the first overlap
			return page;
			
//			PShape border = page.getBorder();
//			float area = border.height * border.width;
		}

		return null;
	}

	public List<Page> getPages() {
		return Collections.unmodifiableList(pages);
	}

	public void setPages(ArrayList<Page> pages) {
		this.pages = pages;
	}
}
