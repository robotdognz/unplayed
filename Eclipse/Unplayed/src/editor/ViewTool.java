package editor;

import game.Game;
import handlers.TextureCache;
import objects.View;
import processing.core.PApplet;
import processing.core.PVector;
import static processing.core.PConstants.*;

public class ViewTool implements Tool {
	PApplet p;
	Editor editor;
	Game game;
	TextureCache texture;
	PVector start; //start of rectangle drawing
	PVector end; //end of rectangle drawing

	public ViewTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
		this.game = editor.game;
		this.texture = editor.texture;
		start = null;
		end = null;
	}

	@Override
	public void touchMoved() {
		if(start == null) {
			start = new PVector(game.point.x, game.point.y);
		}else {
			end = new PVector(game.point.x, game.point.y);
		}

	}

	@Override
	public void touchEnded() {
		// if the stored first point is not null
		// confirm the second point (unless it's the same as the first point)
		// place the view into the world
		if(start != null && end != null) {
			View newView = new View(p, (int)start.x, (int)start.y, (int)(end.x-start.x), (int)(end.y-start.y));
			game.views.add(newView);
			start = null;
			end = null;
		}
	}

	@Override
	public void draw() {
		// draw the current selection area
		
		if(start != null && end != null) {
			p.rectMode(CORNERS);
			p.noFill();
			p.stroke(0,0,255);
			p.strokeWeight(4);
			//TODO: check if you can draw rectangles backwards in CORNERS mode
			if(start.x < end.x && start.y < end.y) {
				p.rect(start.x, start.y, end.x, end.y);
			}
			p.rectMode(CORNER);
		}
	}

}
