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
import ui.MenuWidget;
import ui.Widget;

//handles all of the logic at the application level
public class GameLogic {
	public PApplet p;
	public Activity activity;
	public Context context;

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
	public float widgetSpacing; //size of gap between widgets

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

		

		// TODO implement this
	}

}
