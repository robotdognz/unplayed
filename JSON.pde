class EditorJSON {
  JSONArray values;

  public EditorJSON() {
    //remember that you can save files to the invisible private directory for the game
    //that would be a good way to stop people from using their level files if they haven't paid
  }

  public void save(Game game) {
    try {
      values = new JSONArray();
      HashSet<Rectangle> objects = new HashSet<Rectangle>();
      game.world.getAll(objects);

      //logic for saving
      for (Rectangle r : objects) {
        JSONObject object = new JSONObject();
        object.setInt("pX", (int) r.getX());
        object.setInt("pY", (int) r.getY());

        if (r instanceof Tile) {
          object.setString("type", "tile");
          object.setString("file", (((Tile) r).getFile()).toString() );
        } else if (r instanceof Piece) {
          object.setString("type", "piece");
          object.setInt("pWidth", (int) r.getWidth());
          object.setInt("pHeight", (int) r.getHeight());
          object.setString("file", (((Piece) r).getFile()).toString() );
        }

        values.setJSONObject(values.size(), object); //add it on to the end
      }

      File file = new File("storage/emulated/0/levels/" + "level" + ".json");
      saveJSONArray(values, file.getAbsolutePath());
      showToast("Level Saved");
    }
    catch (Exception e) {
      showToast(e.getMessage());
    }
  }


  public void load(Game game) {
    try {
      File file = new File("storage/emulated/0/levels/" + "level" + ".json");
      values = loadJSONArray(file);

      HashSet<Rectangle> objects = new HashSet<Rectangle>();

      //logic for loading
      for (int i = 0; i < values.size(); i++) {
        JSONObject object = values.getJSONObject(i); 

        int pX = object.getInt("pX");
        int pY = object.getInt("pY");
        String type = object.getString("type");
        File textureFile = new File(object.getString("file"));

        if (type.equals("tile")) {  //if it is a tile
          Tile t = new Tile(textureFile, pX, pY);
          objects.add(t);
        } else if (type.equals("piece")) {  //if it is a piece
          int pWidth = object.getInt("pWidth");
          int pHeight = object.getInt("pHeight");
          Piece p = new Piece(textureFile, pX, pY, pWidth, pHeight);
          objects.add(p);
        }
      }

      //game.world = new Quadtree(game.startingWorld);
      game.world.clear();
      for (Rectangle r : objects) {
        game.world.insert(r);
      }
      showToast("Level Loaded");
    }
    catch(Exception e) {
      showToast(e.getMessage());
    }
  }
}
