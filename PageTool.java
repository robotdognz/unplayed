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
import editor.Editor.editorMode;

public class PageTool extends AreaTool {
	private Converter convert;
	private Game game;
	private PageView pageView;

	private Page currentPage;

	// TODO: currently only does adding, no erase or select

	public PageTool(PApplet p, Editor editor) {
		super(p, editor);
		this.game = editor.game;
		this.pageView = game.getPageView();
		this.convert = editor.convert;
		currentPage = null;
	}

	@Override
	public void touchMoved() {
		if (!editor.showPageView) {// views
			if (editor.eMode == editorMode.ADD) {
				super.touchMoved();
			}
		} else { // pages
			if (editor.eMode == editorMode.ADD) {
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
	}

	@Override
	public void touchEnded(PVector touch) {
		if (!editor.showPageView) { // views
			if (editor.eMode == editorMode.ADD) {
				// get the result of the area tool
				super.touchEnded(touch);
				Rectangle result = (Rectangle) super.getResult();
				if (result != null) {
					View newView = new View(p, (int) result.getX(), (int) result.getY(), (int) result.getWidth(),
							(int) result.getHeight());
					game.views.add(newView);
				}
			}
		} else {// pages
			if (editor.eMode == editorMode.ADD) {
				//editor.currentView != null && 
				if (currentPage != null) { // if there is something to create a page from
					pageView.addPage(currentPage);
				}
			}else if (editor.eMode == editorMode.SELECT) {
				selectPage();
			}
			currentPage = null;
		}
	}
	
	private void selectPage() {
		PVector mouse = convert.screenToLevel(p.mouseX, p.mouseY);
		Page found = pageView.getPage(mouse.x, mouse.y);
		if(found != null) {
			PApplet.println("Page found");
			editor.selected = found;
		}
	}

	@Override
	public void draw() {
		if (!editor.showPageView) { // views
			super.draw();
		} else { // pages
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
