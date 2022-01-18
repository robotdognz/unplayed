package misc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import editor.Editor;
import game.Game;
import handlers.TextureCache;
import objects.Background;
import objects.Editable;
import objects.Event;
import objects.Image;
import objects.Page;
import objects.Rectangle;
import objects.Tile;
import objects.View;
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
			saveBackgrounds(values, editor);

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

	private void saveCameraChanges(JSONArray values, Editor editor) { // TODO: rename this
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();
		editor.game.world.getAll(worldObjects);

		for (Rectangle r : worldObjects) {
			if (r instanceof PlayerStart) {
				JSONObject object = new JSONObject();
				object.setString("name", ((Event) r).getName());
				object.setInt("pX", (int) r.getX());
				object.setInt("pY", (int) r.getY());
				object.setString("type", "PlayerStart");
				saveTile(values, ((PlayerStart) r).getRequired());
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

	private void saveBackgrounds(JSONArray values, Editor editor) {
		List<Background> backgrounds = editor.game.getPageView().getBackgrounds();

		for (Background background : backgrounds) {
			JSONObject object = new JSONObject();
			object.setString("type", "background");
			object.setInt("centerX", (int) background.getPosition().x);
			object.setInt("centerY", (int) background.getPosition().y);
			object.setFloat("size", background.getSize());
			object.setFloat("angle", background.getAngle());
			object.setBoolean("flipH", background.isFlippedH());
			object.setBoolean("flipV", background.isFlippedV());

			object.setString("file", background.getFile().toString());

			values.setJSONObject(values.size(), object); // add it on to the end

		}

	}

	public void load(Game game, String path) {
		try {
			values = p.loadJSONArray(path);

			// clear old level
			game.world.clear(); // remove old world objects
			game.placed.clear(); // removed tiles that have been inserted into slots
			game.removed.clear(); // remove tiles that have become the player
			game.clearPlayerStart(); // remove the player

			// box2d
			game.buildWorld();

			// load new level
			loadTiles(values, game);
			loadWorldObjects(values, game);
			loadCameraChanges(values, game);
			loadViews(values, game);
			loadBackgrounds(values, game);

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
				Tile t = new Tile(game.box2d, texture, textureFile, pX, pY);
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
					PlayerEnd pe = new PlayerEnd(game, texture, name, pX, pY);
					pe.setLevelEnd(end);
					pe.setNewPlayerArea(newPlayerArea);
					worldObjects.add(pe);
				} else if (type.equals("PlayerDeath")) {
					String name = object.getString("name");
					PlayerDeath pd = new PlayerDeath(game, texture, name, pX, pY);
					worldObjects.add(pd);
				} else if (type.equals("Spike")) {
					String name = object.getString("name");
					Spike s = new Spike(game, texture, name, pX, pY, 0);
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

	private void loadCameraChanges(JSONArray values, Game game) { // TODO: rename this
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();
		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("PlayerStart")) {
				int pX = object.getInt("pX");
				int pY = object.getInt("pY");
				String name = object.getString("name");
				PlayerStart ps = new PlayerStart(game, texture, name, pX, pY);
				worldObjects.add(ps); // add the player start to the world
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
					if (viewPages.size() > 0) {
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
	}

	private void loadBackgrounds(JSONArray values, Game game) {
		ArrayList<Background> backgrounds = new ArrayList<Background>();

		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("background")) {
				int centerX = object.getInt("centerX");
				int centerY = object.getInt("centerY");
				PVector center = new PVector(centerX, centerY);

				float size = object.getFloat("size");
				float angle = object.getFloat("angle");
				boolean flipH = object.getBoolean("flipH");
				boolean flipV = object.getBoolean("flipV");

				File textureFile = new File(object.getString("file"));

				Background background = new Background(p, texture, textureFile, center);

				if (flipH) {
					background.flipH();
				}
				if (flipV) {
					background.flipV();
				}
				background.setSize(size);
				background.setAngle(angle);

				backgrounds.add(background);
			}

		}

		game.getPageView().setBackgrounds(backgrounds);
	}

}
