package game;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import camera.Camera;
import camera.FreeCamera;
import controllers.Controller;
import controllers.PlayerControl;
import editor.Editor;
import handlers.TextureCache;
import menus.Menu;
import misc.Converter;
import misc.DoToast;
import misc.KetaiGesture;
import misc.Vibe;
import processing.core.*;
import processing.event.TouchEvent;
import ui.MenuWidget;
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
		Widget menuW = new MenuWidget(p, editor, null);
		widgets.add(menuW);
		widgetSpacing = p.width / (widgets.size() + 1);
	}
	
	public void step() {}

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

		// game
		if (!gPaused) { // step the game if it is not paused
			// step editor or game controller depending on editor toggle
			if (editorToggle) {
				editor.step(touches);
			} else {
				controller.step(touches);
			}
			game.step(); // step game
		}
		game.draw(); // draw the game

		if (editorToggle) {
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

	// TODO: move all of the other methods in Unplayed here (touchMoved(), etc)
}
