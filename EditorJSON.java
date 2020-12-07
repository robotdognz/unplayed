package misc;

import java.io.File;
import java.util.HashSet;

import game.Game;
import handlers.TextureCache;
import objects.CameraChange;
import objects.Event;
import objects.Image;
import objects.PlayerDeath;
import objects.Rectangle;
import objects.Tile;
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

	public void save(Game game) {
		try {
			values = new JSONArray();
			HashSet<Rectangle> objects = new HashSet<Rectangle>();
			game.world.getAll(objects);

			// logic for saving TODO: should be turned into seprate methods for each
			// rectangle type
			for (Rectangle r : objects) {
				JSONObject object = new JSONObject();
				object.setInt("pX", (int) r.getX());
				object.setInt("pY", (int) r.getY());
				object.setInt("pWidth", (int) r.getWidth());
				object.setInt("pHeight", (int) r.getHeight());

				if (r instanceof Tile) { // tiles
					object.setString("type", "tile");
					object.setString("file", (((Tile) r).getFile()).toString());
				} else if (r instanceof Image) { // images
					object.setString("type", "image");
					object.setString("file", (((Image) r).getFile()).toString());
				} else if (r instanceof Event) { // events
					object.setString("name", ((Event) r).getName());
					if (r instanceof PlayerDeath) {
						object.setString("type", "PlayerDeath");
					} else if (r instanceof CameraChange) {
						object.setString("type", "CameraChange");
						object.setFloat("cameraTopLeftX", ((CameraChange) r).getCameraTopLeft().x);
						object.setFloat("cameraTopLeftY", ((CameraChange) r).getCameraTopLeft().y);
						object.setFloat("cameraBottomRightX", ((CameraChange) r).getCameraBottomRight().x);
						object.setFloat("cameraBottomRightY", ((CameraChange) r).getCameraBottomRight().y);
						object.setFloat("cameraZoom", ((CameraChange) r).getCameraZoom());
						object.setFloat("edgeZoom", ((CameraChange) r).getEdgeZoom());
					}
				}

				values.setJSONObject(values.size(), object); // add it on to the end
			}

			File file = new File("storage/emulated/0/levels/" + "level" + ".json");
			p.saveJSONArray(values, file.getAbsolutePath());
			toast.showToast("Level Saved");
		} catch (Exception e) {
			toast.showToast(e.getMessage());
		}
	}

	public void load(Game game) {
		try {
			File file = new File("storage/emulated/0/levels/" + "level" + ".json");
			values = PApplet.loadJSONArray(file);

			HashSet<Rectangle> objects = new HashSet<Rectangle>();

			// logic for loading
			for (int i = 0; i < values.size(); i++) {
				JSONObject object = values.getJSONObject(i);
				int pX = object.getInt("pX");
				int pY = object.getInt("pY");
				int pWidth = object.getInt("pWidth");
				int pHeight = object.getInt("pHeight");
				String type = object.getString("type");

				if (type.equals("tile")) { // if it is a tile
					File textureFile = new File(object.getString("file"));
					Tile t = new Tile(texture, textureFile, pX, pY);
					objects.add(t);
				} else if (type.equals("image")) { // if it is an image
					File textureFile = new File(object.getString("file"));
					Image p = new Image(texture, textureFile, pX, pY, pWidth, pHeight);
					objects.add(p);
				} else if (type.equals("PlayerDeath")) {
					String name = object.getString("name");
					PlayerDeath pd = new PlayerDeath(texture, name, pX, pY);
					objects.add(pd);
				} else if (type.equals("CameraChange")) {
					String name = object.getString("name");
					PVector cameraTopLeft = new PVector(object.getFloat("cameraTopLeftX"),
							object.getFloat("cameraTopLeftY"));
					PVector cameraBottomRight = new PVector(object.getFloat("cameraBottomRightX"),
							object.getFloat("cameraBottomRightY"));
					float cameraZoom = object.getFloat("cameraZoom");
					float edgeZoom = object.getFloat("edgeZoom");
					CameraChange cc = new CameraChange(texture, name, pX, pY, pWidth, pHeight, cameraTopLeft, cameraBottomRight,
							cameraZoom, edgeZoom);
					objects.add(cc);
				}
			}

			game.world.clear();
			for (Rectangle r : objects) {
				game.world.insert(r);
			}
			toast.showToast("Level Loaded");
		} catch (Exception e) {
			toast.showToast(e.getMessage());
		}
	}
}
