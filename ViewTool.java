package editor.tools;

import game.Game;
import game.PageView;
import misc.Converter;
import objects.Page;
import objects.Rectangle;
import objects.View;
import processing.core.PApplet;
import processing.core.PVector;

import editor.Editor;

public class ViewTool extends AreaTool {
	private Converter convert;
	private Game game;
	private PageView pageView;

	private Page currentPage;

	// TODO: currently only does adding, no erase or select

	public ViewTool(PApplet p, Editor editor) {
		super(p, editor);
		this.game = editor.game;
		this.pageView = game.getPageView();
		this.convert = editor.convert;
		currentPage = null;
	}

	@Override
	public void touchMoved() {
		if (!editor.showPageView) {// if in game view
			super.touchMoved();
		} else { // if in page view
			if (editor.currentView != null) {
				if (currentPage == null) {
					float snapNo = 10;
					PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
					// round so blocks snap to grid
					float finalX = Math.round((placement.x - 50) / snapNo) * snapNo;
					float finalY = Math.round((placement.y - 50) / snapNo) * snapNo;
					PVector center = new PVector(finalX, finalY);
					currentPage = new Page(p, game, editor.currentView.getTopLeft(),
							editor.currentView.getBottomRight(), center);
				} else {
					float snapNo = 10;
					PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
					// round so blocks snap to grid
					float finalX = Math.round((placement.x - 50) / snapNo) * snapNo;
					float finalY = Math.round((placement.y - 50) / snapNo) * snapNo;
					PVector center = new PVector(finalX, finalY);
					currentPage.setPosition(center);
				}
			}
		}
	}

	@Override
	public void touchEnded(PVector touch) {
		if (!editor.showPageView) { // if we're in the game view
			// get the result of the area tool
			super.touchEnded(touch);
			Rectangle result = (Rectangle) super.getResult();
			if (result != null) {
				View newView = new View(p, (int) result.getX(), (int) result.getY(), (int) result.getWidth(),
						(int) result.getHeight());
				game.views.add(newView);
			}
		} else {// if we're in the page view
			if (editor.currentView != null && currentPage != null) { // if there is something to create a page from
				pageView.addPage(currentPage);
				currentPage = null;
			}
		}
	}

	@Override
	public void draw() {
		if (!editor.showPageView) {
			super.draw();
		} else {
			if (currentPage != null) {
				currentPage.step();
				currentPage.draw(10); // TODO: get scale
			}
		}
	}

	@Override
	public Object getResult() {
		return null;
	}

}
