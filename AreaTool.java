package editor.tools;

import static processing.core.PConstants.*;
import java.util.ArrayList;
import editor.Editor;
import editor.Tool;
import editor.Editor.editorMode;
import game.AppLogic;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;
import controllers.EditorControl;
import objects.View;

public class AreaTool implements Tool {
	protected PApplet p;
	protected Editor editor;

	private Rectangle start;
	private Rectangle end;

	private Rectangle result;

	public Rectangle edit; // the rectangle being edited

	public AreaTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;

		start = null;
		end = null;
		result = null;
	}

	@Override
	public void touchMoved(PVector touch) {
		if (editor.eMode == editorMode.ADD) {
			if (start == null) {
				// if there is no start, make a new start
				start = editor.point.copy();
			} else if (end == null) {
				// if there is no end, make a new end
				end = editor.point.copy();
			} else {
				// if there is a working end, update it
				end.setTopLeft(editor.point.getX(), editor.point.getY());
			}
		} else if (editor.eMode == editorMode.ERASE) {

		} else if (editor.eMode == editorMode.SELECT) { // resize rectangle
			if (edit != null && editor.point != null) {
				if (PVector.dist(edit.getTopLeft(), editor.point.getTopLeft()) < 150) { // 200
					// prevent adjustments that swap the corners
					if (editor.point.getTopLeft().x < edit.getBottomRight().x - 100
							&& editor.point.getTopLeft().y < edit.getBottomRight().y - 100) {
						edit.adjustTopLeft(editor.point.getTopLeft());
					}
				}
				if (PVector.dist(edit.getBottomRight(), editor.point.getBottomRight()) < 150) { // 200
					// prevent adjustments that swap the corners
					if (editor.point.getBottomRight().x > edit.getTopLeft().x + 100
							&& editor.point.getBottomRight().y > edit.getTopLeft().y + 100) {
						edit.setBottomRight(editor.point.getBottomRight());
					}
				}
			}
		}

	}

	@Override
	public void touchEnded(PVector touch) {
		if (editor.eMode == editorMode.ADD) {
			// if there is both a start and an end
			if (start != null && end != null) {
				// figure out which point is the topleft/bottomright and create/add the view

				// top left corner
				PVector topLeft = new PVector();
				if (start.getX() < end.getX()) {
					topLeft.x = start.getX();
				} else if (start.getX() > end.getX()) {
					topLeft.x = end.getX();
				} else {
					start = null;
					end = null;
					return;
				}
				if (start.getY() < end.getY()) {
					topLeft.y = start.getY();
				} else if (start.getY() > end.getY()) {
					topLeft.y = end.getY();
				} else {
					start = null;
					end = null;
					return;
				}
				// bottom right corner
				PVector bottomRight = new PVector();
				if (end.getBottomRight().x > start.getBottomRight().x) {
					bottomRight.x = end.getBottomRight().x;
				} else if (end.getBottomRight().x < start.getBottomRight().x) {
					bottomRight.x = start.getBottomRight().x;
				} else {
					start = null;
					end = null;
					return;
				}
				if (end.getBottomRight().y > start.getBottomRight().y) {
					bottomRight.y = end.getBottomRight().y;
				} else if (end.getBottomRight().y < start.getBottomRight().y) {
					bottomRight.y = start.getBottomRight().y;
				} else {
					start = null;
					end = null;
					return;
				}
				// make the rectangle
				Rectangle r = new Rectangle((int) topLeft.x, (int) topLeft.y, (int) (bottomRight.x - topLeft.x),
						(int) (bottomRight.y - topLeft.y));
				result = r;

				start = null;
				end = null;
			}
		} else if (editor.eMode == editorMode.ERASE) {

		} else if (editor.eMode == editorMode.SELECT) {

		}

	}

	@Override
	public void draw() {
		float currentScale = AppLogic.convert.getScale();

		if (start != null && end != null) {

			// top left corner
			PVector topLeft = new PVector();
			if (start.getX() < end.getX()) {
				topLeft.x = start.getX();
			} else {
				topLeft.x = end.getX();
			}
			if (start.getY() < end.getY()) {
				topLeft.y = start.getY();
			} else {
				topLeft.y = end.getY();
			}
			// bottom right corner
			PVector bottomRight = new PVector();
			if (end.getBottomRight().x > start.getBottomRight().x) {
				bottomRight.x = end.getBottomRight().x;
			} else {
				bottomRight.x = start.getBottomRight().x;
			}
			if (end.getBottomRight().y > start.getBottomRight().y) {
				bottomRight.y = end.getBottomRight().y;
			} else {
				bottomRight.y = start.getBottomRight().y;
			}

			p.rectMode(CORNERS);
			p.noFill();
			p.stroke(255, 0, 0);
			// calculate the line thickness of the selection area
			int strokeWeight = (int) currentScale / 2;
			p.strokeWeight(strokeWeight);
			p.rect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
			p.rectMode(CORNER);
		}
		if (edit != null && editor.controller instanceof EditorControl && editor.eMode == editorMode.SELECT) {
			p.noStroke();
			p.fill(255, 0, 0);
			p.rectMode(CENTER);
			// calculate the size of the view resize drag points
			int resizeBoxSize = (int) currentScale * 4;
			p.rect(edit.getX(), edit.getY(), resizeBoxSize, resizeBoxSize); // topLeft
			p.rect(edit.getBottomRight().x, edit.getBottomRight().y, resizeBoxSize, resizeBoxSize); // bottomRight
			p.rectMode(CORNER);
		}
	}

	@Override
	public Object getResult() {
		Rectangle toReturn = result;
		result = null;
		return toReturn;
	}

	@Override
	public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
	}

	@Override
	public void onRotate(float x, float y, float angle) {
	}

}
