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
      JSONObject platform = new JSONObject();

      platform.setInt("pX", (int) r.getX());
      platform.setInt("pY", (int) r.getY());
      platform.setInt("pWidth", (int) r.getWidth());
      platform.setInt("pHeight", (int) r.getHeight());

      values.setJSONObject(values.size(), platform); //add it on to the end
    }

    //remember that you can save files to the invisible private directory for the game
    //that would be a good way to stop people from using their level files if they haven't paid

    try {
      File file = new File("storage/emulated/0/levels/" + "level" + ".json");
      saveJSONArray(values, file.getAbsolutePath());
      showToast("Composition saved");
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
        JSONObject platform = values.getJSONObject(i); 

        int pX = platform.getInt("pX");
        int pY = platform.getInt("pY");
        int pWidth = platform.getInt("pWidth");
        int pHeight = platform.getInt("pHeight");

        Platform p = new Platform(pX, pY, pWidth, pHeight);
        objects.add(p);
      }

      game.world.clear();
      for (Rectangle r : objects) {
        game.world.insert(r);
      }
    }
    catch(Exception e) {
      showToast(e.getMessage());
    }
  }
}
