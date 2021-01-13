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
import objects.events.CameraCollider;
import objects.events.PlayerDeath;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import objects.events.Spike;
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
	}

	public void save(Editor editor, String path) {
		try {
			values = new JSONArray();

			saveWorldObjects(values, editor);
			saveCameraChanges(values, editor);
			saveRemoved(values, editor);
			saveViews(values, editor);
//			savePages(values, editor);

			File file;
			if (path.matches(".+.unplayed$")) {
				file = new File(path);
			} else {
				// delete the file made by the file explorer
				File f = new File(path);
				if (f.exists()) {
					f.delete();
				}
				// create the file
				file = new File(path + ".unplayed");
			}

			p.saveJSONArray(values, file.getAbsolutePath());
			if (toast != null) {
				toast.showToast("Level Saved");
			}
		} catch (Exception e) {
			if (toast != null) {
				toast.showToast(e.getMessage());
			}
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
				if (editor.game.placed != null) {
					if (editor.game.placed.contains(r)) {
						continue;
					}
				}
				object.setString("type", "tile");
				object.setString("file", (((Tile) r).getFile()).toString());
			} else if (r instanceof Image) { // images
				object.setString("type", "image");
				object.setString("file", (((Image) r).getFile()).toString());
			} else if (r instanceof Event) { // events
				object.setString("name", ((Event) r).getName());
				if (r instanceof CameraChange || r instanceof CameraCollider) {
					continue;
				}
				if (r instanceof PlayerEnd) { // PlayerEnd
					object.setString("type", "PlayerEnd");
					object.setBoolean("end", ((PlayerEnd) r).getLevelEnd());
					object.setFloat("newPlayerX", ((PlayerEnd) r).getNewPlayerArea().getTopLeft().x);
					object.setFloat("newPlayerY", ((PlayerEnd) r).getNewPlayerArea().getTopLeft().y);
					object.setFloat("newPlayerWidth", ((PlayerEnd) r).getNewPlayerArea().getWidth());
					object.setFloat("newPlayerHeight", ((PlayerEnd) r).getNewPlayerArea().getHeight());
					saveTile(values, ((PlayerEnd) r).getRequired());
				} else if (r instanceof PlayerDeath) {
					object.setString("type", "PlayerDeath");
				} else if (r instanceof Spike) {
					object.setString("type", "Spike");
					object.setFloat("angle", ((Spike) r).getAngle());
				}
			}

			values.setJSONObject(values.size(), object); // add it on to the end
		}
	}

	private void saveCameraChanges(JSONArray values, Editor editor) {
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();
		editor.game.world.getAll(worldObjects);

		for (Rectangle r : worldObjects) {
			if (r instanceof CameraChange) {
				JSONObject object = new JSONObject();
				object.setString("name", ((Event) r).getName());
				object.setInt("pX", (int) r.getX());
				object.setInt("pY", (int) r.getY());
				object.setInt("pWidth", (int) r.getWidth());
				object.setInt("pHeight", (int) r.getHeight());
				object.setFloat("cameraTopLeftX", ((CameraChange) r).getCameraTopLeft().x);
				object.setFloat("cameraTopLeftY", ((CameraChange) r).getCameraTopLeft().y);
				object.setFloat("cameraBottomRightX", ((CameraChange) r).getCameraBottomRight().x);
				object.setFloat("cameraBottomRightY", ((CameraChange) r).getCameraBottomRight().y);
				object.setFloat("cameraZoom", ((CameraChange) r).getCameraZoom());
				object.setFloat("edgeZoom", ((CameraChange) r).getEdgeZoom());
				object.setInt("color", ((CameraChange) r).getColor());
				JSONArray colliders = new JSONArray();
				for (Rectangle rr : worldObjects) {
					// if this is a collider and it belongs to the current CameraChange
					if (!(rr instanceof CameraCollider)) {
						continue;
					}
					if (((CameraCollider) rr).getCamera().equals(r)) {
						JSONObject colliderObject = new JSONObject();
						colliderObject.setInt("pX", (int) rr.getX());
						colliderObject.setInt("pY", (int) rr.getY());
						colliders.setJSONObject(colliders.size(), colliderObject);
					}
				}
				object.setJSONArray("colliders", colliders);

				if (r instanceof PlayerStart) {
					object.setString("type", "PlayerStart");
					saveTile(values, ((PlayerStart) r).getRequired());
				} else {
					object.setString("type", "CameraChange");
				}

				values.setJSONObject(values.size(), object); // add it on to the end
			}

		}
	}

	private void saveTile(JSONArray values, Tile tile) {
		if (tile == null) {
			return;
		}
		JSONObject object = new JSONObject();
		object.setString("type", "tile");
		object.setString("file", tile.getFile().toString());
		object.setInt("pX", (int) tile.getX());
		object.setInt("pY", (int) tile.getY());
		object.setInt("pWidth", (int) tile.getWidth());
		object.setInt("pHeight", (int) tile.getHeight());
		object.setBoolean("flipH", tile.isFlippedH());
		object.setBoolean("flipV", tile.isFlippedV());
		object.setFloat("angle", tile.getAngle());
		values.setJSONObject(values.size(), object); // add it on to the end
	}

	private void saveRemoved(JSONArray values, Editor editor) {
		ArrayList<Tile> removed = editor.game.removed;
		ArrayList<Tile> placed = editor.game.placed;
		if (removed != null) {
			for (Tile t : removed) {
				// if this tile was placed by a PlayerEnd, don't save it
				if (placed.contains(t)) {
					continue;
				}
				JSONObject object = new JSONObject();
				object.setInt("pX", (int) t.getX());
				object.setInt("pY", (int) t.getY());
				object.setInt("pWidth", (int) t.getWidth());
				object.setInt("pHeight", (int) t.getHeight());
				object.setBoolean("flipH", t.isFlippedH());
				object.setBoolean("flipV", t.isFlippedV());
				object.setFloat("angle", t.getAngle());
				object.setString("type", "tile");
				object.setString("file", t.getFile().toString());
				values.setJSONObject(values.size(), object); // add it on to the end
			}
		}

	}

	private void saveViews(JSONArray values, Editor editor) {
		ArrayList<View> views = editor.game.views;
		List<Page> pages = editor.game.getPageView().getPages();

		for (View view : views) {
			JSONObject object = new JSONObject();
			object.setString("type", "view");
			object.setInt("color", view.getColor());
			object.setInt("pX", (int) view.getX());
			object.setInt("pY", (int) view.getY());
			object.setInt("pWidth", (int) view.getWidth());
			object.setInt("pHeight", (int) view.getHeight());

			JSONArray viewPages = new JSONArray();
			for (Page page : pages) {
				// if this page belongs to this view
				if (view.equals(page.getView())) {
					JSONObject pageObject = new JSONObject();
					pageObject.setInt("centerX", (int) page.getPosition().x);
					pageObject.setInt("centerY", (int) page.getPosition().y);
					pageObject.setFloat("size", page.getSize());
					pageObject.setFloat("angle", page.getAngle());
					pageObject.setBoolean("flipH", page.isFlippedH());
					pageObject.setBoolean("flipV", page.isFlippedV());
					pageObject.setBoolean("showPlayer", page.showPlayer);
					pageObject.setBoolean("showObstacles", page.showObstacles);
					pageObject.setBoolean("showTiles", page.showTiles);
					pageObject.setBoolean("showImages", page.showImages);

					viewPages.setJSONObject(viewPages.size(), pageObject);
				}
			}
			object.setJSONArray("pages", viewPages);

			values.setJSONObject(values.size(), object); // add it on to the end
		}

	}

//	private void savePages(JSONArray values, Editor editor) {
//		List<Page> pages = editor.game.getPageView().getPages();
//		for (Page page : pages) {
//			JSONObject object = new JSONObject();
//			object.setString("type", "page");
//			object.setInt("centerX", (int) page.getPosition().x);
//			object.setInt("centerY", (int) page.getPosition().y);
//			object.setFloat("size", page.getSize());
//			object.setFloat("angle", page.getAngle());
//			object.setBoolean("flipH", page.isFlippedH());
//			object.setBoolean("flipV", page.isFlippedV());
//			object.setInt("pX", (int) page.getX());
//			object.setInt("pY", (int) page.getY());
//			object.setInt("pWidth", (int) page.getWidth());
//			object.setInt("pHeight", (int) page.getHeight());
//
//			object.setBoolean("showPlayer", page.showPlayer);
//			object.setBoolean("showObstacles", page.showObstacles);
//			object.setBoolean("showTiles", page.showTiles);
//			object.setBoolean("showImages", page.showImages);
//
//			values.setJSONObject(values.size(), object); // add it on to the end
//		}
//	}

	public void load(Game game, String path) {
		try {
			values = p.loadJSONArray(path);

			// clear old level
			game.world.clear(); // remove old world objects
			game.placed.clear(); // removed tiles that have been inserted into slots
			game.removed.clear(); // remove tiles that have become the player
			game.clearPlayerStart(); // remove the player

			// load new level
			loadTiles(values, game);
			loadWorldObjects(values, game);
			loadCameraChanges(values, game);
			loadViews(values, game);
//			loadPages(values, game);

			if (toast != null) {
				toast.showToast("Level Loaded");
			}
		} catch (Exception e) {
			if (toast != null) {
				toast.showToast(e.getMessage());
			}
		}
	}

	private void loadTiles(JSONArray values, Game game) {
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();
		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("tile")) {
				int pX = object.getInt("pX");
				int pY = object.getInt("pY");
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
			}
		}
		for (Rectangle r : worldObjects) {
			game.world.insert(r);
		}
	}

	private void loadWorldObjects(JSONArray values, Game game) {
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();

		// logic for loading
		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("image") || type.equals("PlayerEnd") || type.equals("PlayerDeath")
					|| type.equals("Spike")) {
				int pX = object.getInt("pX");
				int pY = object.getInt("pY");
				int pWidth = object.getInt("pWidth");
				int pHeight = object.getInt("pHeight");

				if (type.equals("image")) { // if it is an image
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
				} else if (type.equals("PlayerEnd")) {
					String name = object.getString("name");
					boolean end = object.getBoolean("end");
					Rectangle newPlayerArea = new Rectangle(object.getFloat("newPlayerX"),
							object.getFloat("newPlayerY"), object.getFloat("newPlayerWidth"),
							object.getFloat("newPlayerHeight"));
					PlayerEnd pe = new PlayerEnd(texture, name, pX, pY, game);
					pe.setLevelEnd(end);
					pe.setNewPlayerArea(newPlayerArea);
					worldObjects.add(pe);
				} else if (type.equals("PlayerDeath")) {
					String name = object.getString("name");
					PlayerDeath pd = new PlayerDeath(texture, name, pX, pY);
					worldObjects.add(pd);
				} else if (type.equals("Spike")) {
					String name = object.getString("name");
					Spike s = new Spike(texture, name, pX, pY);
					try {
						float angle = object.getFloat("angle");
						s.setAngle(angle);
					} catch (Exception e) {

					}
					worldObjects.add(s);
				}
			}
		}

		for (Rectangle r : worldObjects) {
			game.world.insert(r);
		}
	}

	private void loadCameraChanges(JSONArray values, Game game) {
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();
		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("CameraChange") || type.equals("PlayerStart")) {
				int pX = object.getInt("pX");
				int pY = object.getInt("pY");
				int pWidth = object.getInt("pWidth");
				int pHeight = object.getInt("pHeight");
				String name = object.getString("name");
				PVector cameraTopLeft = new PVector(object.getFloat("cameraTopLeftX"),
						object.getFloat("cameraTopLeftY"));
				PVector cameraBottomRight = new PVector(object.getFloat("cameraBottomRightX"),
						object.getFloat("cameraBottomRightY"));
				float cameraZoom = object.getFloat("cameraZoom");
				float edgeZoom = object.getFloat("edgeZoom");
				int color = object.getInt("color");
				JSONArray colliders = object.getJSONArray("colliders");
				CameraChange cc;
				if (type.equals("PlayerStart")) {
					cc = new PlayerStart(p, texture, name, pX, pY, game);
				} else {
					cc = new CameraChange(p, texture, name, pX, pY);
				}
				cc.setWidth(pWidth);
				cc.setHeight(pHeight);
				cc.setCameraTopLeft(cameraTopLeft);
				cc.setCameraBottomRight(cameraBottomRight);
				cc.setCameraZoom(cameraZoom);
				cc.setEdgeZoom(edgeZoom);
				cc.setColor(color);

				worldObjects.add(cc); // add the camera change to the world

				// create and add any colliders it has
				try {
					if (colliders.size() > 0) {
						for (int j = 0; j < colliders.size(); j++) {
							JSONObject jCollider = colliders.getJSONObject(j);
							int cX = jCollider.getInt("pX");
							int cY = jCollider.getInt("pY");
							CameraCollider collider = new CameraCollider(cc, cX, cY);
							worldObjects.add(collider);
						}
					}
				} catch (Exception e) {

				}
			}
		}
		for (Rectangle r : worldObjects) {
			game.world.insert(r);
		}
	}

	private void loadViews(JSONArray values, Game game) {
		ArrayList<View> views = new ArrayList<View>();
		ArrayList<Page> pages = new ArrayList<Page>();

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

				JSONArray viewPages = object.getJSONArray("pages");
				try {
					if (pages.size() > 0) {
						for (int j = 0; j < viewPages.size(); j++) {
							JSONObject jPage = viewPages.getJSONObject(j);
							int centerX = jPage.getInt("centerX");
							int centerY = jPage.getInt("centerY");
							PVector center = new PVector(centerX, centerY);

							float size = jPage.getFloat("size");
							float angle = jPage.getFloat("angle");
							boolean flipH = jPage.getBoolean("flipH");
							boolean flipV = jPage.getBoolean("flipV");

							Page page = new Page(p, game, v, center);

							if (flipH) {
								page.flipH();
							}
							if (flipV) {
								page.flipV();
							}
							page.setSize(size);
							page.setAngle(angle);

							// exclusion booleans
							try {
								page.showPlayer = jPage.getBoolean("showPlayer");
								page.showObstacles = jPage.getBoolean("showObstacles");
								page.showTiles = jPage.getBoolean("showTiles");
								page.showImages = jPage.getBoolean("showImages");
							} catch (Exception e) {

							}

							pages.add(page);
						}
					}
				} catch (Exception e) {
					if (toast != null) {
						toast.showToast(e.getMessage());
					}
				}

			}
		}
		game.setViews(views);
		game.getPageView().setPages(pages);
		PApplet.println(game.getPageView().getPages().size() + " : " + pages.size());
	}

//	private void loadPages(JSONArray values, Game game) {
//		// list of pages
//		ArrayList<Page> pages = new ArrayList<Page>();
//		for (int i = 0; i < values.size(); i++) {
//			JSONObject object = values.getJSONObject(i);
//			String type = object.getString("type");
//			if (type.equals("page")) {
//				int centerX = object.getInt("centerX");
//				int centerY = object.getInt("centerY");
//				float size = object.getFloat("size");
//				float angle = object.getFloat("angle");
//				boolean flipH = object.getBoolean("flipH");
//				boolean flipV = object.getBoolean("flipV");
//				int pX = object.getInt("pX");
//				int pY = object.getInt("pY");
//				int pWidth = object.getInt("pWidth");
//				int pHeight = object.getInt("pHeight");
//				PVector topLeft = new PVector(pX, pY);
//				PVector bottomRight = new PVector(pX + pWidth, pY + pHeight);
//				PVector center = new PVector(centerX, centerY);
//				Page page = new Page(p, game, topLeft, bottomRight, center);
//				if (flipH) {
//					page.flipH();
//				}
//				if (flipV) {
//					page.flipV();
//				}
//				page.setSize(size);
//				page.setAngle(angle);
//
//				// exclusion booleans
//				try {
//					page.showPlayer = object.getBoolean("showPlayer");
//					page.showObstacles = object.getBoolean("showObstacles");
//					page.showTiles = object.getBoolean("showTiles");
//					page.showImages = object.getBoolean("showImages");
//				} catch (Exception e) {
//
//				}
//
//				pages.add(page);
//			}
//		}
//		game.getPageView().setPages(pages);
//	}
}
