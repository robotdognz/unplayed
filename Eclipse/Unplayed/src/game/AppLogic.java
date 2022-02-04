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
import objects.Rectangle;
import processing.core.*;
import processing.event.TouchEvent;
import ui.LaunchMenu;
import ui.LoadingMenu;
import ui.Menu;
import ui.Widget;

//handles all of the logic at the application level
public class AppLogic {
	private static PApplet p;
	private static Activity activity;
	private static Context context;

	public static KetaiGesture gesture;
	public static FileChooser files;
	public static Vibe vibe;
	static public Converter convert;
	public static TextureCache texture;

	static private Menu menu; // current menu
	static private boolean menuAdded = false;
	static private boolean menuRemoved = false;

	public static boolean editorToggle; // is the editor enabled
	static public Game game; // holds the game class
	public static Controller controller; // holds the current controller
	public static Editor editor; // holds the editor
	public static ArrayList<PVector> touches; // all the on screen touches
	public static PVector lastTouch; // the last on screen touch

	public static ArrayList<Widget> widgets;
	public static float widgetSpacing; // size of gap between widgets
	public static float widgetHeight;

	private static ArrayList<File> levels;
	private static int currentLevel;

	public AppLogic(PApplet papp, Activity masterActivity, Context masterContext) {
		p = papp;
		activity = masterActivity;
		context = masterContext;
		editorToggle = false;
	}

	static public void init() {
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
		game = new Game(p, camera, texture, convert);
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

		menu = new LaunchMenu(p);
		game.getPageView().initCamera();

		p.background(100);

		// print android api version
		PApplet.println(android.os.Build.VERSION.SDK_INT);
	}

	static public void getLevels() {
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

	static public void newGame() {
		currentLevel = 0;

		if (levels != null && levels.size() > currentLevel) {
			loadingScreen();
		}
	}

	static public void nextLevel() {
		currentLevel++;

		if (levels != null && levels.size() > currentLevel) {
			loadingScreen();
		} else {
			titleScreen();
		}
	}

	static public void startLevel() {
		if (editor != null) {
			// we're in the editor
			game.startGame();
			removeMenu();
		} else {
			// not in the editor
			EditorJSON json = new EditorJSON(p, texture, null);

			if (levels != null && levels.size() > currentLevel) {
				json.load(game, levels.get(currentLevel).toString());
				game.startGame(); // needed to reset game after loading in new level
				// step the game and camera so we can pull values out of the level
				game.step(0.1f);
				game.cameraStep(0.1f);

				// TODO: offset newly loaded pages and backgrounds to just off screen
				// calculate offset
				Rectangle pageArea = game.getPageView().getArea();
				Rectangle playerArea = game.getPageView().getPlayerVisibleArea();
				Rectangle menuArea = menu.getArea();

				// get edge of pageArea closest to center of playerArea
				PVector playerAreaCenter = playerArea.getRectangleCenter();
				PVector pageAreaCenter = pageArea.getRectangleCenter();
				PVector diff = pageAreaCenter.copy();
				diff.x -= playerAreaCenter.x;
				diff.y -= playerAreaCenter.y;

				PVector menuCenter = menuArea.getRectangleCenter();

				float offsetX = 0;
				float offsetY = 0;
				offsetX = menuCenter.x - playerAreaCenter.x;
				offsetY = menuCenter.y - playerAreaCenter.y;
				
//				if (Math.abs(diff.x) >= Math.abs(diff.y)) {
//					// more difference on x axis than y axis
//					if (diff.x <= 0) {
//						// move to off right edge of pageArea
//
//						// difference between rightEdge of pageArea and leftEdge of menu
//
//					} else {
//						// move to off left edge of pageArea
//
//					}
//					
//					offsetX = menuCenter.x - pageAreaCenter.x;
//					offsetY = menuCenter.y - pageAreaCenter.y;
//
//				} else {
//					// more difference on y axis than x axis
//					if (diff.y <= 0) {
//						// move to off top edge of pageArea
//
//					} else {
//						// move to off bottom edge of pageArea
//
//					}
//
//					offsetX = menuCenter.x - pageAreaCenter.x;
//					offsetY = menuCenter.y - pageAreaCenter.y;
//				}

				game.getPageView().offsetAll(offsetX, offsetY);

				// TODO: force draw all assets

				removeMenu();
			}
		}
	}

	static public void titleScreen() {
		Menu temp = new LaunchMenu(p);
		Rectangle pageArea = game.getPageView().getArea();

		temp.buldPageMenu(game.getPageView().getPageCamera().getCenter(), pageArea, game.getPageView().getPageCamera());
		addMenu(temp);
	}

	static public void loadingScreen() {
		Menu temp = new LoadingMenu(p);
		Rectangle pageArea = game.getPageView().getArea();

		temp.buldPageMenu(game.getPageView().getPageCamera().getCenter(), pageArea, game.getPageView().getPageCamera());
		addMenu(temp);
	}

	static public void toggleEditor() {
		editorToggle = !editorToggle;
		if (editorToggle) {
			Camera camera = new FreeCamera();
			DoToast toast = new DoToast(activity);
			editor = new Editor(p, files, texture, game, camera, convert, toast);
		}
		menu = null;
		game.getPageView().clearMenus();
	}

	static public Editor getEditor() {
		return editor;
	}

	static public void setUri(Uri uri) {
		files.setUri(uri);
	}

	static public void draw(float deltaTime) {
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
			} // else if (menu instanceof LoadingMenu) {
//				game.step(deltaTime); // step game and physics
//			}
		}
		game.cameraStep(deltaTime); // step camera etc

		// draw the game
		if ((editor != null && !editorToggle) || (editor != null && Editor.showPageView) || (editor == null)) {
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

	static public void touchStarted() {
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

	static public void touchEnded() {
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

	static public void touchMoved() {
		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.touchMoved(lastTouch, touches);
			} else {
				controller.touchMoved(lastTouch, touches);
			}
		}
	}

	static public void onTap(float x, float y) {
		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.onTap(x, y);
			} else {
				// controller.onTap(x, y);
			}
		}
	}

	static public void onDoubleTap(float x, float y) {

	}

	static public void onFlick(float x, float y, float px, float py, float v) {
		// x/y start of flick
		// px/yx end of flick
		// v velocity of flick
	}

	static public void onLongPress(float x, float y) {

	}

	static public void onPinch(float x, float y, float d) {
		if (menu == null) {
			if (editorToggle && editor != null) {
				editor.onPinch(touches, x, y, d);
			} else {
				controller.onPinch(touches, x, y, d);
			}
		}
	}

	static public void onRotate(float x, float y, float angle) {
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

	static public void addMenu(Menu newMenu) {
		// menus act as a linked list
		// add to head
		if (menu != null) {
			Menu temp = menu;
			menu = newMenu;
			menu.child = temp;
		} else {
			menu = newMenu;
		}

		menuAdded = true;
		menuRemoved = false;
	}

	static public void removeMenu() {
		// menus act as a linked list
		// remove from tail
		if (menu != null) {

			if (menu.child != null) {
				Menu temp = menu;

				while (temp.child.child != null) {
					temp = temp.child;
				}

				temp.child = null;

			} else {
				menu = null;
			}

		}

		menuAdded = false;
		menuRemoved = true;
	}

	static public void previousMenu() {
		// menus act as a linked list
		// swap the first and second menus in the list around
		if (menu != null && menu.child != null) {
			// menu.child will become the new menu
			// menu will become the new menu.child
			Menu temp = menu; // old head
			menu = menu.child; // old body, becomes new head
			temp.child = menu.child; // make all children of old body children of old head
			menu.child = temp; // make old head child of new head
		}

		// might need these?
		menuAdded = true;
		menuRemoved = false;
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

	static public void quit() {
		activity.finish();
	}

	static public void quitPurge() {
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
