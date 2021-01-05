package game;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.content.res.AssetManager;
import camera.Camera;
import camera.FreeCamera;
import controllers.Controller;
import controllers.PlayerControl;
import editor.Editor;
import editor.uitop.WidgetPauseMenu;
import handlers.TextureCache;
import misc.Converter;
import misc.DoToast;
import misc.FileChooser;
import misc.KetaiGesture;
import misc.Vibe;
import processing.core.*;
import processing.event.TouchEvent;
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

	public AppLogic(PApplet p, Activity activity, Context context) {
		this.p = p;
		this.activity = activity;
		this.context = context;
		gPaused = false;
		menu = null;
		editorToggle = true;

	}

	public void init() {
		// prepare data files

//		AssetManager test = context.getAssets();
//		try {
//			String path = "ui";
//			String[] files = test.list(path);
//
//			File testFile = new File(path + '/' + files[0]);
//			PImage temp = p.loadImage(testFile.toString());// path + '/' + files[0]);
//			PApplet.println(temp + " load worked");
//
//			String doublePath = "ui/widgets";
//			String[] widgetFiles = test.list(doublePath);
//			PApplet.println(widgetFiles.length);
//			for (String s : widgetFiles) {
//				PApplet.println(s);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
		Path base = Paths.get(p.sketchPath(""));
//		File dir = new File(p.sketchPath("")); // context.getFilesDir();
		File tileFolder = new File(p.dataPath("tiles") + '/');
		File relative = new File(base.relativize(tileFolder.toPath()).toString());
		PApplet.println(relative);
		
		
		File[] allFiles = tileFolder.listFiles();
		for (File f : allFiles) {
			PApplet.println(f.toString());
			PApplet.println(base.relativize(f.toPath()));
		}
		Path image = base.relativize(allFiles[0].toPath());
		PImage temp = p.loadImage(image.toAbsolutePath().toString());// path + '/' + files[0]);
		PApplet.println("loading worked: " + image);

		gPaused = false;
		editorToggle = true;
		menu = null;
		touches = new ArrayList<PVector>();
		lastTouch = new PVector(0, 0);
		widgets = new ArrayList<Widget>();

		texture = new TextureCache(p);
		gesture = new KetaiGesture(p);
		files = new FileChooser(activity);
		vibe = new Vibe(context);

		Camera camera = new FreeCamera(); // new GameCamera();
		convert = new Converter(p, camera); // camera converter
		game = new Game(p, this, camera, vibe, texture, convert);
		texture.passGame(game);
		controller = new PlayerControl(p, game);
		DoToast toast = new DoToast(activity);
		editor = new Editor(p, files, texture, game, camera, convert, toast);

		//// setup non editor widget(s)
		Widget menuW = new WidgetPauseMenu(p, editor, null);
		widgets.add(menuW);
		widgetSpacing = p.width / (widgets.size() + 1);
	}

	public Editor getEditor() {
		return editor;
	}

	public void setUri(Uri uri) {
		files.setUri(uri);
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

		// step the game and editor
		if (!gPaused) { // step the game if it is not paused
			// step editor or game controller depending on editor toggle
			if (editorToggle && editor != null) {
				editor.step(touches);
			} else {
				controller.step(touches);
			}
			game.step(); // step game
		}

		// step/draw the game
		if (!editorToggle || editor == null || (editor != null && editor.showPageView)) {
//			if (!gPaused) {
//				game.step(); // step game
//			}
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

		// gesture.
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

	// quit

	public void quit() {
		activity.finish();
	}

	public void quitPurge() {
		// TODO: update this so it deletes directories etc

		// purge resources
		// root
		File rootDir = new File(p.dataPath("") + '/');
		File[] rootPaths = rootDir.listFiles();
		for (File file : rootPaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// tiles
		File tileDir = new File(p.dataPath("tiles") + '/');
		File[] tilePaths = tileDir.listFiles();
		for (File file : tilePaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// images
		File imageDir = new File(p.dataPath("images") + '/');
		File[] imagePaths = imageDir.listFiles();
		for (File file : imagePaths) {
			if (file.exists()) {
				file.delete();
			}
		}
		// events
		File eventDir = new File(p.dataPath("events") + '/');
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
