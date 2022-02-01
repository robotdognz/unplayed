package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import camera.Camera;
import camera.FreeCamera;
import camera.GameCamera;
import controllers.Controller;
import controllers.PlayerControl;
import editor.Editor;
import editor.uitop.WidgetPauseMenu;
import handlers.TextureCache;
import misc.Converter;
import misc.DoToast;
import misc.EditorJSON;
import misc.FileChooser;
import misc.KetaiGesture;
import misc.Vibe;
import processing.core.*;
import processing.event.TouchEvent;
import ui.LaunchMenu;
import ui.Menu;
import ui.Widget;

//handles all of the logic at the application level
public class AppLogic {
	private PApplet p;
	private Activity activity;
	private Context context;

	public KetaiGesture gesture;
	public FileChooser files;
	public Vibe vibe;
	static public Converter convert;
	public TextureCache texture;

	static private Menu menu; // current menu
	static private boolean menuAdded = false;
	static private boolean menuRemoved = false;

	public static boolean editorToggle; // is the editor enabled
	static public Game game; // holds the game class
	public Controller controller; // holds the current controller
	public Editor editor; // holds the editor
	public ArrayList<PVector> touches; // all the on screen touches
	public PVector lastTouch; // the last on screen touch

	public ArrayList<Widget> widgets;
	public float widgetSpacing; // size of gap between widgets
	public float widgetHeight;

	private boolean runGame;
	private ArrayList<File> levels;
	private int currentLevel;

	public AppLogic(PApplet p, Activity activity, Context context) {
		this.p = p;
		this.activity = activity;
		this.context = context;
		editorToggle = false;
	}

	public void init() {
		editorToggle = false; // editor is closed on startup
		touches = new ArrayList<PVector>();
		lastTouch = new PVector(0, 0);
		widgets = new ArrayList<Widget>();

		texture = new TextureCache(p, context);
		gesture = new KetaiGesture(p);
		files = new FileChooser(activity);
		vibe = new Vibe(context);

		Camera camera = new GameCamera(); // new FreeCamera();
		convert = new Converter(p);
		game = new Game(p, this, camera, texture, convert);
		texture.passGame(game);
		controller = new PlayerControl(p, game);
//		DoToast toast = new DoToast(activity);
//		editor = new Editor(p, files, texture, game, camera, convert, toast);
		editor = null;

		// setup non editor widget(s)
		Widget menuW = new WidgetPauseMenu(p, game, null);
		widgets.add(menuW);
		widgetSpacing = p.width / (widgets.size() + 1);
		widgetHeight = p.displayWidth / 12; // 120

		getLevels();

		runGame = false;
		menu = new LaunchMenu(p, this);
		menu.buldPageMenu();
		
		p.background(100);

		// print android api version
		PApplet.println(android.os.Build.VERSION.SDK_INT);
	}

	public void getLevels() {
		levels = new ArrayList<File>();
		// generate all the relative file paths
		try {
			// App mode

			AssetManager am = context.getAssets();
			String levelPath = "levels";
			String[] levelStrings = am.list(levelPath);

			if (levelStrings.length == 0) {
				throw new IOException();
			}

			// make relative files from all of the level strings
			for (int i = 0; i < levelStrings.length; i++) {
				levels.add(new File(levelPath + '/' + levelStrings[i]));
			}

		} catch (IOException e) {
			// Preview mode

			String base = p.sketchPath("");
			File levelPath = new File(base + "/levels" + '/');

			File[] absoluteFiles = levelPath.listFiles();

			// make relative files from all of the tile strings
			for (int i = 0; i < absoluteFiles.length; i++) {
				String relativeFile = absoluteFiles[i].toString();
				relativeFile = relativeFile.replace(base + '/', "");
				levels.add(new File(relativeFile));
			}
		}

		Collections.sort(levels);
	}

	public void startGame() {
		currentLevel = 0;
		EditorJSON json = new EditorJSON(p, texture, null);

		if (levels != null && levels.size() > currentLevel) {
			json.load(game, levels.get(currentLevel).toString());
			new GameCamera();
			game.startGame();
			runGame = true;
			menu = null;
			game.getPageView().initCamera();
			// TODO: this is where the transition from title screen to game would happen
		}

	}

	public void nextLevel() {
		currentLevel++;
		EditorJSON json = new EditorJSON(p, texture, null);

		if (levels.size() > currentLevel) {
			json.load(game, levels.get(currentLevel).toString());
			game.startGame();
			// TODO: this is where level transition menu would be created
		} else {
			runGame = false;
			init();
			// TODO: this is where it would start the transition back to the title screen
		}

	}

	public void toggleEditor() {
		editorToggle = !editorToggle;
		if (editorToggle) {
			if (editor == null) {
				Camera camera = new FreeCamera();
				DoToast toast = new DoToast(activity);
				editor = new Editor(p, files, texture, game, camera, convert, toast);
			}

		}
		menu = null;
	}

	public Editor getEditor() {
		return editor;
	}

	public void setUri(Uri uri) {
		files.setUri(uri);
	}

	public void draw(float deltaTime) {
		// This is the step method for the whole game, as well as the draw method

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

		// step the game and editor
		if (editorToggle && editor != null) {
			// step editor or game controller depending on editor toggle
			editor.step(touches);
			if (menu == null) {
				game.step(deltaTime); // step game and physics
			}
		} else {
			if (menu == null) {
				controller.step(touches);
				game.step(deltaTime); // step game and physics
			}
		}
		game.cameraStep(deltaTime); // step camera etc

		// draw the game
		if ((editor != null && !editorToggle) || (editor != null && Editor.showPageView)
				|| (editor == null && runGame)) { // || editor == null
			game.draw(); // draw the game
		}

		if (editorToggle && editor != null) {
			editor.draw(deltaTime, lastTouch, menu);
		} else {
			for (int i = 0; i < widgets.size(); i++) {
				widgets.get(i).draw(deltaTime, widgetSpacing * (i + 1), widgetHeight);
				widgets.get(i).updateActive();
				if (menu == null) {
					widgets.get(i).hover(lastTouch);
				}
			}
		}

		// draw the menu
		if (menu != null) {
			if (!Camera.getGame()) {
				menu.draw();
			}
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
				editor.touchMoved(lastTouch, touches);
			} else {
				controller.touchMoved(lastTouch, touches);
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

	public void onDoubleTap(float x, float y) {

	}

	public void onFlick(float x, float y, float px, float py, float v) {
		// x/y start of flick
		// px/yx end of flick
		// v velocity of flick
	}

	public void onLongPress(float x, float y) {

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

	public void onRotate(float x, float y, float angle) {
		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.onRotate(x, y, angle);
			} else {
				controller.onRotate(x, y, angle);
			}
		}
	}

	// menu

	static public boolean hasMenu() {
		if (menu != null) {
			return true;
		}
		return false;
	}

	static public void setMenu(Menu newMenu) {
		menu = newMenu;
		menuAdded = true;
		menuRemoved = false;
	}

	static public void removeMenu() {
		menu = null;
		menuAdded = false;
		menuRemoved = true;
	}

	static public Menu getMenu() {
		return menu;
	}

	static public boolean menuAdded() {
		boolean temp = menuAdded;
		menuAdded = false;
		return temp;
	}

	static public boolean menuRemoved() {
		boolean temp = menuRemoved;
		menuRemoved = false;
		return temp;
	}

	// quit

	public void quit() {
		activity.finish();
	}

	public void quitPurge() {
		// should update this so it deletes directories etc

		// purge resources
		// root
		File rootDir = new File(p.dataPath("") + '/');
		File[] rootPaths = rootDir.listFiles();
		for (File file : rootPaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// levels
		File levelDir = new File(p.dataPath("levels") + '/');
		File[] levelPaths = levelDir.listFiles();
		for (File file : levelPaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// tiles
		File tileDir = new File(p.dataPath("unplayed_tiles") + '/');
		File[] tilePaths = tileDir.listFiles();
		for (File file : tilePaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// images
		File imageDir = new File(p.dataPath("unplayed_images") + '/');
		File[] imagePaths = imageDir.listFiles();
		for (File file : imagePaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// background images
		File backgroundDir = new File(p.dataPath("unplayed_backgrounds") + '/');
		File[] backgroundPaths = backgroundDir.listFiles();
		for (File file : backgroundPaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// events
		File eventDir = new File(p.dataPath("unplayed_events") + '/');
		File[] eventPaths = eventDir.listFiles();
		for (File file : eventPaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// ui
		File uiDir = new File(p.dataPath("ui") + '/');
		File[] uiPaths = uiDir.listFiles();
		for (File file : uiPaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// widgets
		File widgetDir = new File(p.dataPath("ui") + '/' + "widgets" + '/');
		File[] widgetPaths = widgetDir.listFiles();
		for (File file : widgetPaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		quit();
	}

}
