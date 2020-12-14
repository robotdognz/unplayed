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

	private PVector start; // start of rectangle drawing
	private PVector end; // end of rectangle drawing

	private Rectangle result;
	
	public Rectangle edit; //the rectangle being edited

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
			if (start == null) {
				//if there is no start, make a new start
				start = new PVector(editor.point.x, editor.point.y);
			} else if (end == null) {
				//if there is no end, make a new end
				end = new PVector(editor.point.x, editor.point.y);
			}else {
				//if there is a working end, update it
				end.x = editor.point.x;
				end.y = editor.point.y;
			}
		} else if (editor.eMode == editorMode.ERASE) {

		} else if (editor.eMode == editorMode.SELECT) {
			if(edit != null) {
				if(PVector.dist(edit.getTopLeft(), touch) < 100) {
					edit.setTopLeft(touch);
				}else if(PVector.dist(edit.getBottomRight(), touch) < 100) {
					edit.setBottomRight(touch);
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
				if (start.x < end.x && start.y < end.y) {
					Rectangle r = new Rectangle((int) start.x, (int) start.y, (int) (end.x - start.x),
							(int) (end.y - start.y));
					result = r;
				} else if (start.x > end.x && start.y < end.y) {
					Rectangle r = new Rectangle((int) end.x, (int) start.y, (int) (start.x - end.x),
							(int) (end.y - start.y));
					result = r;
				} else if (start.x < end.x && start.y > end.y) {
					Rectangle r = new Rectangle((int) start.x, (int) end.y, (int) (end.x - start.x),
							(int) (start.y - end.y));
					result = r;
				} else if (start.x > end.x && start.y > end.y) {
					Rectangle r = new Rectangle((int) end.x, (int) end.y, (int) (start.x - end.x),
							(int) (start.y - end.y));
					result = r;
				}
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
			p.rectMode(CORNERS);
			p.noFill();
			p.stroke(255, 0, 0);
			p.strokeWeight(4);
			p.rect(start.x, start.y, end.x, end.y);
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
