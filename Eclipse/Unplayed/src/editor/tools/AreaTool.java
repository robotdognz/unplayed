package editor.tools;

import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.CORNERS;

import editor.Editor;
import editor.Tool;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class AreaTool implements Tool {
	protected PApplet p;
	protected Editor editor;
	// private Converter convert;

	private PVector start; // start of rectangle drawing
	private PVector end; // end of rectangle drawing

	private Rectangle result;

	public AreaTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;

		start = null;
		end = null;
		result = null;
	}

	@Override
	public void touchMoved() {
//		if (!editor.showPageView) {// if in game view
			if (start == null) {
				start = new PVector(editor.point.x, editor.point.y);
			} else {
				end = new PVector(editor.point.x + 100, editor.point.y + 100);
			}
//		} else { // if in page view
//
//		}

	}

	@Override
	public void touchEnded(PVector touch) {
		//if (!editor.showPageView) { // if we're in the game view
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
//		} else { // if we're in the page view
//
//		}

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

}
