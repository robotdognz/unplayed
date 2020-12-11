package game;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import camera.Camera;
import camera.FreeCamera;
import controllers.Controller;
import controllers.PlayerControl;
import editor.Editor;
import editor.uitop.WidgetPauseMenu;
import handlers.TextureCache;
import misc.Converter;
import misc.DoToast;
import misc.KetaiGesture;
import misc.Vibe;
import processing.core.*;
import processing.event.TouchEvent;
import ui.Menu;
import ui.Widget;

//handles all of the logic at the application level
public class GameLogic {
	private PApplet p;
	private Activity activity;
	private Context context;

	public KetaiGesture gesture;
	public Vibe vibe;
	public Converter convert;
	public TextureCache texture;

	public boolean gPaused; // is the game paused
	public Menu menu; // current menu
	public boolean editorToggle; // is the editor enabled
	public Game game; // holds the game class
	public Controller controller; // holds the current controller
	public Editor editor; // holds the editor
	public ArrayList<PVector> touches; // all the on screen touches
	public PVector lastTouch; // the last on screen touch

	public ArrayList<Widget> widgets;
	public float widgetSpacing; // size of gap between widgets

	public GameLogic(PApplet p, Activity activity, Context context) {
		this.p = p;
		this.activity = activity;
		this.context = context;
		gPaused = false;
		menu = null;
		editorToggle = true;

	}

	public void init() {
		gPaused = false;
		editorToggle = true;
		menu = null;
		touches = new ArrayList<PVector>();
		lastTouch = new PVector(0, 0);
		widgets = new ArrayList<Widget>();

		texture = new TextureCache(p);
		gesture = new KetaiGesture(p);
		vibe = new Vibe(context);

		Camera camera = new FreeCamera(); // new GameCamera();
		convert = new Converter(p, camera); // camera converter
		game = new Game(p, camera, vibe, texture, convert);
		game.passGameLogic(this);
		controller = new PlayerControl(p, game);
		DoToast toast = new DoToast(activity);
		editor = new Editor(p, texture, game, camera, convert, toast);

		//// setup non editor widget(s)
		Widget menuW = new WidgetPauseMenu(p, editor, null);
		widgets.add(menuW);
		widgetSpacing = p.width / (widgets.size() + 1);
	}

	public void step() {
	}

	public void draw() {
		// touch screen
		touches.clear();
		for (TouchEvent.Pointer t : p.touches) {
			touches.add(new PVector(t.x, t.y));
		}
		if (p.touches.length > 0) {
			lastTouch = new PVector(p.touches[p.touches.length - 1].x, p.touches[p.touches.length - 1].y);
		} else {
			lastTouch = new PVector(0, 0);
		}

		// step the game ad editor
		if (!gPaused) { // step the game if it is not paused
			// step editor or game controller depending on editor toggle
			if (editorToggle && editor != null) {
				editor.step(touches);
			} else {
				controller.step(touches);
			}
			game.step(); // step game
		}
		
		//draw the game
		if (!editorToggle || editor == null || (editor != null && editor.showPageView)) {
			game.draw(); // draw the game
		}

		if (editorToggle && editor != null) {
			editor.draw(lastTouch, menu);
		} else {
			for (int i = 0; i < widgets.size(); i++) {
				widgets.get(i).draw(widgetSpacing * (i + 1), 120);
				widgets.get(i).updateActive();
				if (menu == null) {
					widgets.get(i).hover(lastTouch);
				}
			}
		}

		// draw the menu
		if (menu != null) {
			menu.draw();
			menu.hover(lastTouch);
		}
	}

	public void touchStarted() {
		// find true last touch
		if (p.touches.length >= touches.size() && p.touches.length > 1) {
			for (int i = 0; i < p.touches.length; i++) {
				boolean match = false;
				for (PVector t : touches) {
					float currentDiff = PApplet
							.sqrt(PApplet.sq(t.x - p.touches[i].x) + PApplet.sq(t.x - p.touches[i].x));
					if (currentDiff < 10) {
						match = true;
					}
				}
				if (!match) { // no match for current touch, so it's new
					lastTouch = new PVector(p.touches[i].x, p.touches[i].y);
				}
			}
		} else if (p.touches.length == 1) {
			lastTouch = new PVector(p.touches[p.touches.length - 1].x, p.touches[p.touches.length - 1].y);
		}

		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.touchStarted(lastTouch);
			} else {
				controller.touchStarted(lastTouch);
			}
		}
	}

	public void touchEnded() {
		if (editorToggle && editor != null) {
			editor.touchEnded(lastTouch);
		} else {
			for (int i = 0; i < widgets.size(); i++) {
				widgets.get(i).click();
			}
		}

		if (menu != null) {
			menu.click();
		}
	}

	public void touchMoved() {
		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.touchMoved(touches);
			} else {
				controller.touchMoved(touches);
			}
		}
	}

	public void onPinch(float x, float y, float d) {
		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.onPinch(touches, x, y, d);
			} else {
				controller.onPinch(touches, x, y, d);
			}
		}
	}

	public void onTap(float x, float y) {
		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.onTap(x, y);
			} else {
				// controller.onTap(x, y);
			}
		}
	}

	public void onFlick(float x, float y, float px, float py, float v) {
		// x/y start of flick
		// px/yx end of flick
		// v velocity of flick
	}

	public void onRotate(float x, float y, float angle) {
		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.onRotate(x, y, angle);
			} else {
				controller.onRotate(x, y, angle);
			}
		}
	}

}
