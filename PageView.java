package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camera.Camera;
import misc.Converter;
import objects.Page;
import processing.core.*;

public class PageView {
	private PApplet p;
	private Converter convert;
	private ArrayList<Page> pages;

	// world camera
	private Camera camera;

	public PageView(PApplet p, Camera camera, Converter convert) {
		this.p = p;
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

		for (Page p : pages) {
			p.draw(currentScale);
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
	
	public Page getPage(float x, float y) {
		
		if(pages.size() < 1) {
			return null;
		}
		for (Page p : pages) {
			PApplet.println("try getPage()" + p);
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
			//return the first overlap
			return p;
		}
		
		return null;
	}

	public List<Page> getPages() {
		return Collections.unmodifiableList(pages);
	}
}
