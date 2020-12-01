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

      //logic for saving TODO: should be turned into sperate methods for each rectangle type
      for (Rectangle r : objects) {
        JSONObject object = new JSONObject();
        object.setInt("pX", (int) r.getX());
        object.setInt("pY", (int) r.getY());
        object.setInt("pWidth", (int) r.getWidth());
        object.setInt("pHeight", (int) r.getHeight());

        if (r instanceof Tile) {  //tiles
          object.setString("type", "tile");
          object.setString("file", (((Tile) r).getFile()).toString() );
        } else if (r instanceof Piece) {    //pieces
          object.setString("type", "piece");
          object.setString("file", (((Piece) r).getFile()).toString() );
        } else if (r instanceof Event) {    //events
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
        int pWidth = object.getInt("pWidth");
        int pHeight = object.getInt("pHeight");
        String type = object.getString("type");

        if (type.equals("tile")) {  //if it is a tile
          File textureFile = new File(object.getString("file"));
          Tile t = new Tile(textureFile, pX, pY);
          objects.add(t);
        } else if (type.equals("piece")) {  //if it is a piece
          File textureFile = new File(object.getString("file"));
          Piece p = new Piece(textureFile, pX, pY, pWidth, pHeight);
          objects.add(p);
        } else if (type.equals("PlayerDeath")) {
          String name = object.getString("name");
          PlayerDeath pd = new PlayerDeath(name, pX, pY);
          objects.add(pd);
        } else if (type.equals("CameraChange")) {
          String name = object.getString("name");
          PVector cameraTopLeft = new PVector(object.getFloat("cameraTopLeftX"), object.getFloat("cameraTopLeftY"));
          PVector cameraBottomRight = new PVector(object.getFloat("cameraBottomRightX"), object.getFloat("cameraBottomRightY"));
          float cameraZoom = object.getFloat("cameraZoom");
          float edgeZoom = object.getFloat("edgeZoom");
          CameraChange cc = new CameraChange(name, pX, pY, pWidth, pHeight, cameraTopLeft, cameraBottomRight, cameraZoom, edgeZoom);
          objects.add(cc);
        }
      }

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
