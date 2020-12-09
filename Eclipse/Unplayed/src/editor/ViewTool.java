package editor;

import game.Game;
import game.Page;
import game.PageView;
import misc.Converter;
import objects.View;
import processing.core.PApplet;
import processing.core.PVector;
import static processing.core.PConstants.*;

public class ViewTool implements Tool {
	private PApplet p;
	private Editor editor;
	private Converter convert;
	private Game game;
	private PageView pageView;
	private PVector start; // start of rectangle drawing
	private PVector end; // end of rectangle drawing

	// TODO: have an AreaSelectTool inside this tool instead of having two copies of
	// TODO: currently only does adding, no erase or select

	public ViewTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
		this.game = editor.game;
		this.pageView = game.getPageView();
		this.convert = editor.convert;
		start = null;
		end = null;
	}

	@Override
	public void touchMoved() {
		if (!game.displayPages) {// if in game view
			if (start == null) {
				start = new PVector(editor.point.x, editor.point.y);
			} else {
				end = new PVector(editor.point.x + 100, editor.point.y + 100);
			}
		} else { // if in page view

		}

	}

	@Override
	public void touchEnded(PVector touch) {
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
			if (editor.currentView != null) { // if there is something to create a page from
				float snapNo = 10;
				PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
				// round so blocks snap to grid
				float finalX = Math.round((placement.x - 50) / snapNo) * snapNo;
				float finalY = Math.round((placement.y - 50) / snapNo) * snapNo;
				PVector center = new PVector(finalX, finalY);
				Page page = new Page(p, game, editor.currentView.getTopLeft(), editor.currentView.getBottomRight(),
						center, 1, 0, false, false);
				pageView.addPage(page);
			}
		}
	}

	@Override
	public void draw() {
		if (start != null && end != null) {
			p.rectMode(CORNERS);
			p.noFill();
			p.stroke(255, 0, 0);
			p.strokeWeight(4);
			p.rect(start.x, start.y, end.x, end.y);
			p.rectMode(CORNER);
		}
	}

}
