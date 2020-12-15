package misc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import editor.Editor;
import game.Game;
import handlers.TextureCache;
import objects.Editable;
import objects.Event;
import objects.Image;
import objects.Page;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.CameraChange;
import objects.events.PlayerDeath;
import objects.events.PlayerStart;
import processing.core.*;
import processing.data.*;

public class EditorJSON {
	PApplet p;
	TextureCache texture;
	DoToast toast;
	JSONArray values;

	public EditorJSON(PApplet p, TextureCache texture, DoToast toast) {
		this.p = p;
		this.texture = texture;
		this.toast = toast;
		// remember that you can save files to the invisible private directory for the
		// game
		// that would be a good way to stop people from using their level files if they
		// haven't paid
	}

	public void save(Editor editor) {
		try {
			values = new JSONArray();

			saveWorldObjects(values, editor);
			saveViews(values, editor);
			savePages(values, editor);

			File file = new File("storage/emulated/0/levels/" + "level" + ".json");
			p.saveJSONArray(values, file.getAbsolutePath());
			toast.showToast("Level Saved");
		} catch (Exception e) {
			toast.showToast(e.getMessage());
		}
	}

	private void saveWorldObjects(JSONArray values, Editor editor) {
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();
		editor.game.world.getAll(worldObjects);

		for (Rectangle r : worldObjects) {
			JSONObject object = new JSONObject();
			object.setInt("pX", (int) r.getX());
			object.setInt("pY", (int) r.getY());
			object.setInt("pWidth", (int) r.getWidth());
			object.setInt("pHeight", (int) r.getHeight());
			if (r instanceof Editable) {
				object.setBoolean("flipH", ((Editable) r).isFlippedH());
				object.setBoolean("flipV", ((Editable) r).isFlippedV());
				object.setFloat("angle", ((Editable) r).getAngle());
			}

			if (r instanceof Tile) { // tiles
				object.setString("type", "tile");
				object.setString("file", (((Tile) r).getFile()).toString());
			} else if (r instanceof Image) { // images
				object.setString("type", "image");
				object.setString("file", (((Image) r).getFile()).toString());
			} else if (r instanceof Event) { // events
				object.setString("name", ((Event) r).getName());
				if (r instanceof PlayerStart) {
					object.setString("type", "PlayerStart");
					object.setFloat("cameraTopLeftX", ((CameraChange) r).getCameraTopLeft().x);
					object.setFloat("cameraTopLeftY", ((CameraChange) r).getCameraTopLeft().y);
					object.setFloat("cameraBottomRightX", ((CameraChange) r).getCameraBottomRight().x);
					object.setFloat("cameraBottomRightY", ((CameraChange) r).getCameraBottomRight().y);
					object.setFloat("cameraZoom", ((CameraChange) r).getCameraZoom());
					object.setFloat("edgeZoom", ((CameraChange) r).getEdgeZoom());
					object.setInt("color", ((CameraChange) r).getColor());
				} else if (r instanceof PlayerDeath) {
					object.setString("type", "PlayerDeath");
				} else if (r instanceof CameraChange) {
					object.setString("type", "CameraChange");
					object.setFloat("cameraTopLeftX", ((CameraChange) r).getCameraTopLeft().x);
					object.setFloat("cameraTopLeftY", ((CameraChange) r).getCameraTopLeft().y);
					object.setFloat("cameraBottomRightX", ((CameraChange) r).getCameraBottomRight().x);
					object.setFloat("cameraBottomRightY", ((CameraChange) r).getCameraBottomRight().y);
					object.setFloat("cameraZoom", ((CameraChange) r).getCameraZoom());
					object.setFloat("edgeZoom", ((CameraChange) r).getEdgeZoom());
					object.setInt("color", ((CameraChange) r).getColor());
				}
			}

			values.setJSONObject(values.size(), object); // add it on to the end
		}
	}

	private void saveViews(JSONArray values, Editor editor) {
		ArrayList<View> views = editor.game.views;

		for (View view : views) {
			JSONObject object = new JSONObject();
			object.setString("type", "view");
			object.setInt("color", view.getColor());
			object.setInt("pX", (int) view.getX());
			object.setInt("pY", (int) view.getY());
			object.setInt("pWidth", (int) view.getWidth());
			object.setInt("pHeight", (int) view.getHeight());

			values.setJSONObject(values.size(), object); // add it on to the end
		}

	}

	private void savePages(JSONArray values, Editor editor) {
		List<Page> pages = editor.game.getPageView().getPages();
		for (Page page : pages) {
			JSONObject object = new JSONObject();
			object.setString("type", "page");
			object.setInt("centerX", (int) page.getPosition().x);
			object.setInt("centerY", (int) page.getPosition().y);
			object.setFloat("size", page.getSize());
			object.setFloat("angle", page.getAngle());
			object.setBoolean("flipH", page.isFlippedH());
			object.setBoolean("flipV", page.isFlippedV());
			object.setInt("pX", (int) page.getX());
			object.setInt("pY", (int) page.getY());
			object.setInt("pWidth", (int) page.getWidth());
			object.setInt("pHeight", (int) page.getHeight());

			values.setJSONObject(values.size(), object); // add it on to the end
		}
	}

	public void load(Game game) {
		try {
			File file = new File("storage/emulated/0/levels/" + "level" + ".json");
			values = PApplet.loadJSONArray(file);

			loadWorldObjects(values, game);
			loadViews(values, game);
			loadPages(values, game);

			toast.showToast("Level Loaded");
		} catch (Exception e) {
			toast.showToast(e.getMessage());
		}
	}

	private void loadWorldObjects(JSONArray values, Game game) {
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();

		// logic for loading
		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("tile") || type.equals("image") || type.equals("PlayerStart") || type.equals("PlayerDeath")
					|| type.equals("CameraChange")) {
				int pX = object.getInt("pX");
				int pY = object.getInt("pY");
				int pWidth = object.getInt("pWidth");
				int pHeight = object.getInt("pHeight");

				if (type.equals("tile")) { // if it is a tile
					File textureFile = new File(object.getString("file"));
					boolean flipH = object.getBoolean("flipH");
					boolean flipV = object.getBoolean("flipV");
					float angle = object.getFloat("angle");
					Tile t = new Tile(texture, textureFile, pX, pY);
					t.setAngle(angle);
					if (flipH) {
						t.flipH();
					}
					if (flipV) {
						t.flipV();
					}
					worldObjects.add(t);
				} else if (type.equals("image")) { // if it is an image
					File textureFile = new File(object.getString("file"));
					boolean flipH = object.getBoolean("flipH");
					boolean flipV = object.getBoolean("flipV");
					float angle = object.getFloat("angle");
					Image im = new Image(texture, textureFile, pX, pY, pWidth, pHeight);
					im.setAngle(angle);
					if (flipH) {
						im.flipH();
					}
					if (flipV) {
						im.flipV();
					}
					worldObjects.add(im);
				} else if (type.equals("PlayerStart")) {
					String name = object.getString("name");
					PVector cameraTopLeft = new PVector(object.getFloat("cameraTopLeftX"),
							object.getFloat("cameraTopLeftY"));
					PVector cameraBottomRight = new PVector(object.getFloat("cameraBottomRightX"),
							object.getFloat("cameraBottomRightY"));
					float cameraZoom = object.getFloat("cameraZoom");
					float edgeZoom = object.getFloat("edgeZoom");
					PlayerStart ps = new PlayerStart(p, texture, name, pX, pY, game);
					ps.setWidth(pWidth);
					ps.setHeight(pHeight);
					ps.setCameraTopLeft(cameraTopLeft);
					ps.setCameraBottomRight(cameraBottomRight);
					ps.setCameraZoom(cameraZoom);
					ps.setEdgeZoom(edgeZoom);
					try {
						int color = object.getInt("color");
						ps.setColor(color);
					} catch (Exception e) {

					}
					worldObjects.add(ps);
				} else if (type.equals("PlayerDeath")) {
					String name = object.getString("name");
					PlayerDeath pd = new PlayerDeath(texture, name, pX, pY);
					worldObjects.add(pd);
				} else if (type.equals("CameraChange")) {
					String name = object.getString("name");
					PVector cameraTopLeft = new PVector(object.getFloat("cameraTopLeftX"),
							object.getFloat("cameraTopLeftY"));
					PVector cameraBottomRight = new PVector(object.getFloat("cameraBottomRightX"),
							object.getFloat("cameraBottomRightY"));
					float cameraZoom = object.getFloat("cameraZoom");
					float edgeZoom = object.getFloat("edgeZoom");
					CameraChange cc = new CameraChange(p, texture, name, pX, pY);
					cc.setWidth(pWidth);
					cc.setHeight(pHeight);
					cc.setCameraTopLeft(cameraTopLeft);
					cc.setCameraBottomRight(cameraBottomRight);
					cc.setCameraZoom(cameraZoom);
					cc.setEdgeZoom(edgeZoom);
					try {
						int color = object.getInt("color");
						cc.setColor(color);
					} catch (Exception e) {

					}
					worldObjects.add(cc);
				}
			}
		}

		game.world.clear();
		for (Rectangle r : worldObjects) {
			game.world.insert(r);
		}
	}

	private void loadViews(JSONArray values, Game game) {
		ArrayList<View> views = new ArrayList<View>();
		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("view")) {
				int pX = object.getInt("pX");
				int pY = object.getInt("pY");
				int pWidth = object.getInt("pWidth");
				int pHeight = object.getInt("pHeight");
				int color = object.getInt("color");
				View v = new View(p, pX, pY, pWidth, pHeight);
				v.setColor(color);
				views.add(v);
			}
		}
		game.setViews(views);
	}

	private void loadPages(JSONArray values, Game game) {
		// list of pages
		ArrayList<Page> pages = new ArrayList<Page>();
		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("page")) {
				int centerX = object.getInt("centerX");
				int centerY = object.getInt("centerY");
				float size = object.getFloat("size");
				float angle = object.getFloat("angle");
				boolean flipH = object.getBoolean("flipH");
				boolean flipV = object.getBoolean("flipV");
				int pX = object.getInt("pX");
				int pY = object.getInt("pY");
				int pWidth = object.getInt("pWidth");
				int pHeight = object.getInt("pHeight");
				PVector topLeft = new PVector(pX, pY);
				PVector bottomRight = new PVector(pX + pWidth, pY + pHeight);
				PVector center = new PVector(centerX, centerY);
				Page page = new Page(p, game, topLeft, bottomRight, center);
				if (flipH) {
					page.flipH();
				}
				if (flipV) {
					page.flipV();
				}
				page.setSize(size);
				page.setAngle(angle);
				pages.add(page);
			}
		}
		game.getPageView().setPages(pages);
	}
}
