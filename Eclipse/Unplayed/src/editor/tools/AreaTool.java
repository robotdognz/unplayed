package editor.tools;

import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.CORNERS;

import java.util.ArrayList;

import editor.Editor;
import editor.Tool;
import misc.Converter;
import editor.Editor.editorMode;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class AreaTool implements Tool {
	protected PApplet p;
	protected Editor editor;
	protected Converter convert;

//	private PVector start; // start of rectangle drawing
//	private PVector end; // end of rectangle drawing
	private Rectangle start;
	private Rectangle end;

	private Rectangle result;

	public Rectangle edit; // the rectangle being edited

	public AreaTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
		this.convert = editor.convert;

		start = null;
		end = null;
		result = null;
	}

	@Override
	public void touchMoved(PVector touch) {
		if (editor.eMode == editorMode.ADD) {
			// TODO: put code in here that takes the start and end from
			if (start == null) {
				// if there is no start, make a new start
//				start = new PVector(editor.point.getX(), editor.point.getY());
				start = editor.point.copy();
			} else if (end == null) {
				// if there is no end, make a new end
//				end = new PVector(editor.point.getX(), editor.point.getY());
				end = editor.point.copy();
			} else {
				// if there is a working end, update it
//				end.x = editor.point.getX();
//				end.y = editor.point.getY();
				end.setTopLeft(editor.point.getX(), editor.point.getY());
			}
		} else if (editor.eMode == editorMode.ERASE) {

		} else if (editor.eMode == editorMode.SELECT) {
			if (edit != null && editor.point != null) {
				if (PVector.dist(edit.getTopLeft(), editor.point.getTopLeft()) < 200) {
					// prevent adjustments that swap the corners
					if (editor.point.getTopLeft().x < edit.getBottomRight().x
							&& editor.point.getTopLeft().y < edit.getBottomRight().y) {
						edit.adjustTopLeft(editor.point.getTopLeft());
					}
				} else if (PVector.dist(edit.getBottomRight(), editor.point.getBottomRight()) < 200) {
					// prevent adjustments that swap the corners
					if (editor.point.getBottomRight().x > edit.getTopLeft().x
							&& editor.point.getBottomRight().y > edit.getTopLeft().y) {
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
//				if (start.x < end.x && start.y < end.y) {
//					Rectangle r = new Rectangle((int) start.x, (int) start.y, (int) (end.x - start.x),
//							(int) (end.y - start.y));
//					result = r;
//				} else if (start.x > end.x && start.y < end.y) {
//					Rectangle r = new Rectangle((int) end.x, (int) start.y, (int) (start.x - end.x),
//							(int) (end.y - start.y));
//					result = r;
//				} else if (start.x < end.x && start.y > end.y) {
//					Rectangle r = new Rectangle((int) start.x, (int) end.y, (int) (end.x - start.x),
//							(int) (start.y - end.y));
//					result = r;
//				} else if (start.x > end.x && start.y > end.y) {
//					Rectangle r = new Rectangle((int) end.x, (int) end.y, (int) (start.x - end.x),
//							(int) (start.y - end.y));
//					result = r;
//				}

//				if (start.getX() < end.getBottomRight().x && start.getY() < end.getBottomRight().x) {
//					Rectangle r = new Rectangle((int) start.getX(), (int) start.getY(),
//							(int) (end.getBottomRight().x - start.getX()),
//							(int) (end.getBottomRight().y - start.getY()));
//					result = r;
//				} else if (start.getBottomRight().x > end.getX() && start.getY() < end.getBottomRight().x) {
//					Rectangle r = new Rectangle((int) end.getX(), (int) start.getY(),
//							(int) (start.getBottomRight().x - end.getX()),
//							(int) (end.getBottomRight().y - start.getY()));
//					result = r;
//				} else if (start.getX() < end.getBottomRight().x && start.getBottomRight().y > end.getY()) {
//					Rectangle r = new Rectangle((int) start.getX(), (int) end.getBottomRight().y,
//							(int) (end.getBottomRight().x - start.getX()),
//							(int) (start.getBottomRight().y - end.getY()));
//					result = r;
//				} else if (start.getBottomRight().x > end.getX() && start.getBottomRight().y > end.getY()) {
//					Rectangle r = new Rectangle((int) end.getX(), (int) end.getY(),
//							(int) (start.getBottomRight().x - end.getX()),
//							(int) (start.getBottomRight().y - end.getY()));
//					result = r;
//				}

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
				//make the rectangle
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
		if (start != null && end != null) {
			
			// top left corner
			PVector topLeft = new PVector();
			if (start.getX() < end.getX()) {
				topLeft.x = start.getX();
			} else {// if (start.getX() > end.getX()) {
				topLeft.x = end.getX();
			}
			if (start.getY() < end.getY()) {
				topLeft.y = start.getY();
			} else {//if (start.getY() > end.getY()) {
				topLeft.y = end.getY();
			}
			// bottom right corner
			PVector bottomRight = new PVector();
			if (end.getBottomRight().x > start.getBottomRight().x) {
				bottomRight.x = end.getBottomRight().x;
			} else {// if (end.getBottomRight().x < start.getBottomRight().x) {
				bottomRight.x = start.getBottomRight().x;
			}
			if (end.getBottomRight().y > start.getBottomRight().y) {
				bottomRight.y = end.getBottomRight().y;
			} else {// if (end.getBottomRight().y < start.getBottomRight().y) {
				bottomRight.y = start.getBottomRight().y;
			}
			
			p.rectMode(CORNERS);
			p.noFill();
			p.stroke(255, 0, 0);
			p.strokeWeight(4);
			// TODO: improve this
//			p.rect(start.getX(), start.getY(), end.getX(), end.getY());
			p.rect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
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
