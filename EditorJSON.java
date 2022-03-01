package misc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import editor.Editor;
import game.AppLogic;
import game.Game;
import handlers.TextureCache;
import objects.Background;
import objects.Editable;
import objects.Event;
import objects.Image;
import objects.Page;
import objects.PageViewObject;
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
			savePlayerStart(values, editor);
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
		AppLogic.game.world.getAll(worldObjects);

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
				if (AppLogic.game.placed != null) {
					if (AppLogic.game.placed.contains(r)) {
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
				if (r instanceof PlayerStart) {
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

	private void savePlayerStart(JSONArray values, Editor editor) {
		HashSet<Rectangle> worldObjects = new HashSet<Rectangle>();
		AppLogic.game.world.getAll(worldObjects);

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
		ArrayList<Tile> removed = AppLogic.game.removed;
		ArrayList<Tile> placed = AppLogic.game.placed;
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
		ArrayList<View> views = AppLogic.game.views;
		List<PageViewObject> pages = AppLogic.game.getPageView().getPageViewObjects();

		for (View view : views) {
			JSONObject object = new JSONObject();
			object.setString("type", "view");
			object.setInt("color", view.getColor());
			object.setInt("pX", (int) view.getX());
			object.setInt("pY", (int) view.getY());
			object.setInt("pWidth", (int) view.getWidth());
			object.setInt("pHeight", (int) view.getHeight());

			JSONArray viewPages = new JSONArray();
			for (PageViewObject pageViewObject : pages) {
				// only check actual pages
				if (!(pageViewObject instanceof Page)) {
					continue;
				}
				Page page = (Page) pageViewObject;

				// if this page belongs to this view
				if (view.equals(page.getView())) {
					JSONObject pageJsonObject = new JSONObject();
					pageJsonObject.setInt("centerX", (int) page.getPosition().x);
					pageJsonObject.setInt("centerY", (int) page.getPosition().y);
					pageJsonObject.setFloat("size", page.getSize());
					pageJsonObject.setFloat("angle", page.getAngle());
					pageJsonObject.setBoolean("flipH", page.isFlippedH());
					pageJsonObject.setBoolean("flipV", page.isFlippedV());
					pageJsonObject.setBoolean("showPlayer", page.showPlayer);
					pageJsonObject.setBoolean("showObstacles", page.showObstacles);
					pageJsonObject.setBoolean("showTiles", page.showTiles);
					pageJsonObject.setBoolean("showImages", page.showImages);

					// TODO: save children of this page
					List<PageViewObject> children = page.getChildren();
					if (children.size() > 0) {
						JSONArray pageChildren = new JSONArray();
						for (PageViewObject child : children) {
							JSONObject pageChild = new JSONObject();
							pageChild.setString("type", child.getName());
							pageChild.setFloat("centerX", (int) child.getPosition().x);
							pageChild.setInt("centerY", (int) child.getPosition().y);
							pageChildren.setJSONObject(pageChildren.size(), pageChild);
						}
						pageJsonObject.setJSONArray("children", pageChildren);
					}

					viewPages.setJSONObject(viewPages.size(), pageJsonObject);
				}
			}
			object.setJSONArray("pages", viewPages);

			values.setJSONObject(values.size(), object); // add it on to the end
		}

	}

	private void saveBackgrounds(JSONArray values, Editor editor) {
		List<PageViewObject> backgrounds = AppLogic.game.getPageView().getPageViewObjects();

		for (PageViewObject backgroundObject : backgrounds) {
			if (!(backgroundObject instanceof Background)) {
				continue;
			}
			Background background = (Background) backgroundObject;

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

//			// clear old level
//			game.world.clear(); // remove old world objects
//			game.placed.clear(); // removed tiles that have been inserted into slots
//			game.removed.clear(); // remove tiles that have become the player
//			game.clearPlayerStart(); // remove the player
			game.emptyGame();

			// box2d
			game.buildWorld();

			// load new level
			loadTiles(values, game);
			loadWorldObjects(values, game);
			loadPlayerStart(values, game);
			game.getPageView().clearPageViewObjects();
			loadViews(values, game);
			loadBackgrounds(values, game);
			loadPageChildren(values, game);

			if (toast != null) {
				toast.showToast("Level Loaded");
			}
		} catch (Exception e) {
			if (toast != null) {
				toast.showToast(e.getMessage());
			}
			e.printStackTrace();
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

	private void loadPlayerStart(JSONArray values, Game game) {
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
		ArrayList<PageViewObject> pages = new ArrayList<PageViewObject>();

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

							// TODO: load children

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
		game.getPageView().addPageViewObjects(pages);

	}

	private void loadBackgrounds(JSONArray values, Game game) {
		ArrayList<PageViewObject> backgrounds = new ArrayList<PageViewObject>();

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

		game.getPageView().addPageViewObjects(backgrounds);
	}

	private void loadPageChildren(JSONArray values2, Game game) {

		ArrayList<PageViewObject> pageViewObjects = new ArrayList<PageViewObject>();

		// TODO Auto-generated method stub
		for (int i = 0; i < values.size(); i++) {
			JSONObject object = values.getJSONObject(i);
			String type = object.getString("type");
			if (type.equals("view")) {
				JSONArray viewPages = object.getJSONArray("pages");
				try {
					if (viewPages.size() > 0) {
						// for each page made from this view, find it in the level
						for (int j = 0; j < viewPages.size(); j++) {
							JSONObject jPage = viewPages.getJSONObject(j);
							int parentCenterX = jPage.getInt("centerX");
							int parentCenterY = jPage.getInt("centerY");
							PVector parentCenter = new PVector(parentCenterX, parentCenterY);

							JSONArray pageChildren = object.getJSONArray("children");
							if (pageChildren.size() < 1) {
								// if this page has no children, go to next page
								continue;
							}

							for (PageViewObject pageViewObject : pageViewObjects) {
								if (!(pageViewObject instanceof Page)) {
									continue;
								}

								Page parentPage = (Page) pageViewObject;

								if (parentPage.getPosition() == parentCenter) {
									// found the matching page in the level

									// find matching children
									for (int k = 0; k < viewPages.size(); k++) {
										// load current child information
										JSONObject jChild = viewPages.getJSONObject(k);
										String jChildType = object.getString("type");
										int childCenterX = jChild.getInt("centerX");
										int childCenterY = jChild.getInt("centerY");
										PVector jChildCenter = new PVector(childCenterX, childCenterY);

										// find matching child in level
										for (PageViewObject pageViewObjectChild : pageViewObjects) {
											if (!jChildType.equals(pageViewObjectChild.getName())) {
												continue;
											}

											if (pageViewObjectChild.getPosition() != jChildCenter) {
												continue;
											}

											// we have a match
											parentPage.addOrRemoveChild(pageViewObjectChild);
										}
									}

								}
							}
						}
					}
				} catch (Exception e) {
					if (toast != null) {
						toast.showToast(e.getMessage());
					}
				}
			}
		}
	}

}
