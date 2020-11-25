class EditorJSON {
  JSONArray values;

  public EditorJSON() {
  }

  public void save(Game game) {
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
      if(r instanceof Platform){
        object.setString("type", "platform");
      }

      values.setJSONObject(values.size(), object); //add it on to the end
    }

    //remember that you can save files to the invisible private directory for the game
    //that would be a good way to stop people from using their level files if they haven't paid

    try {
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
        if(type.equals("platform")) {
          Platform p = new Platform(pX, pY, pWidth, pHeight);
          objects.add(p);
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
