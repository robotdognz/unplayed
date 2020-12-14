package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camera.Camera;
import handlers.TextureCache;
import misc.Converter;
import objects.Page;
import objects.Rectangle;
import processing.core.*;
import static processing.core.PConstants.*;

public class PageView {
	private PApplet p;
	private TextureCache texture;
	private Converter convert;
	private ArrayList<Page> pages;

	// world camera
	private Camera camera;

	public PageView(PApplet p, Camera camera, TextureCache texture, Converter convert) {
		this.p = p;
		this.texture = texture;
		this.convert = convert;
		this.camera = camera;
		pages = new ArrayList<Page>();
	}

	public void draw() {
		p.background(100);
		// draw the desk

		p.pushMatrix(); // start working at game scale
		p.translate(p.width / 2, p.height / 2); // set x=0 and y=0 to the middle of the screen

		// camera
		p.scale((float) p.width / (float) camera.getScale()); // width/screen fits the level scale to the screen
		p.scale(camera.getSubScale()); // apply offset for tall screen spaces
		p.translate(-camera.getCenter().x, -camera.getCenter().y); // moves the view around the level

		float currentScale = convert.getScale();

		p.imageMode(CENTER);
		PImage temp = texture.getDeskBehind();
		float ratio = (float) temp.height / (float) temp.width;
		p.image(temp, 0, 0, p.width, p.width * ratio);
		// p.image(temp, p.width / 2, p.height / 2, p.width, p.width * ratio);

		// p.background(100);

		for (Page p : pages) {
			p.draw(currentScale);
		}

		// draw desk shading
		p.imageMode(CENTER);
		p.image(texture.getDeskInfront(), 0, 0, p.width, p.width * ratio);
		// p.image(texture.getDeskInfront(), p.width / 2, p.height / 2, p.width, p.width
		// * ratio);

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
