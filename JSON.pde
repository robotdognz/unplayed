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
        object.setInt("pWidth", (int) r.getWidth());
        object.setInt("pHeight", (int) r.getHeight());
        if (r instanceof Platform) {
          object.setString("type", "tile");
        } else if (r instanceof Piece) {
          object.setString("type", "piece");
          object.setString("file", (((Piece) r).getFile()).toString() );
        }

        values.setJSONObject(values.size(), object); //add it on to the end
      }

      File file = new File("storage/emulated/0/levels/" + "level" + ".json");
      saveJSONArray(values, file.getAbsolutePath());
      showToast("Level Saved: " + objects.size());
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
        int pWidth = object.getInt("pWidth");
        int pHeight = object.getInt("pHeight");
        String type = object.getString("type");
        if (type.equals("tile")) {  //if it is a tile
          Platform p = new Platform(pX, pY, pWidth, pHeight);
          objects.add(p);
        } else if (type.equals("piece")) {  //if it is a piece
          File pieceFile = new File(object.getString("file"));
          Piece p = new Piece(pieceFile, pX, pY, pWidth, pHeight);
          objects.add(p);
        }
      }

      game.world.clear();
      int count = 0;
      for (Rectangle r : objects) {
        game.world.insert(r);
        count++;
      }
      showToast("Level Loaded: " + count);
    }
    catch(Exception e) {
      showToast(e.getMessage());
    }
  }
}
