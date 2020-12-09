package editor;

import game.Game;
import objects.View;
import processing.core.PApplet;
import processing.core.PVector;
import static processing.core.PConstants.*;

public class ViewTool implements Tool {
	private PApplet p;
	private Editor editor;
	private Game game;
	private PVector start; // start of rectangle drawing
	private PVector end; // end of rectangle drawing

	public ViewTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
		this.game = editor.game;
		// this.texture = editor.texture;
		start = null;
		end = null;
	}

	@Override
	public void touchMoved() {
		if (!game.displayPages) {// if in game view
			if (start == null) {
				start = new PVector(editor.point.x, editor.point.y);
			} else {
				end = new PVector(editor.point.x+100, editor.point.y+100);
			}
		} else { // if in page view

		}

	}

	@Override
	public void touchEnded() {
		if (!game.displayPages) { // if we're in the game view
			// if there is both a start and an end
			if (start != null && end != null) {
				// figure out which point is the topleft/bottomright and create/add the view
				if (start.x < end.x && start.y < end.y) {
					View newView = new View(p, (int) start.x, (int) start.y, (int) (end.x - start.x),
							(int) (end.y - start.y));
					game.views.add(newView);
				} else if (start.x > end.x && start.y < end.y) {
					View newView = new View(p, (int) end.x, (int) start.y, (int) (start.x - end.x),
							(int) (end.y - start.y));
					game.views.add(newView);
				} else if (start.x < end.x && start.y > end.y) {
					View newView = new View(p, (int) start.x, (int) end.y, (int) (end.x - start.x),
							(int) (start.y - end.y));
					game.views.add(newView);
				} else if (start.x > end.x && start.y > end.y) {
					View newView = new View(p, (int) end.x, (int) end.y, (int) (start.x - end.x),
							(int) (start.y - end.y));
					game.views.add(newView);
				}
				start = null;
				end = null;
			}
		} else {// if we're in the page view

		}
	}

	@Override
	public void draw() {
		// TODO: needs to adjust to game scale and position

		if (start != null && end != null) {
			p.rectMode(CORNERS);
			p.noFill();
			p.stroke(0, 0, 255);
			p.strokeWeight(4);
			p.rect(start.x, start.y, end.x, end.y);
			p.rectMode(CORNER);
		}
	}

}
