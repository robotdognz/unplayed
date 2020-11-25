class EditorJSON {
  JSONArray values;

  public EditorJSON() {
  }

  public void save(Game game) {
    values = new JSONArray();
    HashSet<Rectangle> objects = new HashSet<Rectangle>();

    game.world.getAll(objects);

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
      //Writer output = null;

      //if (hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
        File file = new File("storage/emulated/0/levels/" + "level" + ".json");
        saveJSONArray(values, file.getAbsolutePath());
        //output = new BufferedWriter(new FileWriter(file));
        //output.write(values.toString());
        //output.close();
        showToast("Composition saved");
      //} else {
      //  showToast("Please enable file writing permissions");
      //  requestPermission("android.permission.WRITE_EXTERNAL_STORAGE");
      //}
    } 
    catch (Exception e) {
      showToast(e.getMessage());
    }
  }


  public void load(Game game) {
    HashSet<Rectangle> objects = new HashSet<Rectangle>();

    try {

      //if (hasPermission("android.permission.READ_EXTERNAL_STORAGE")) {
        File file = new File("storage/emulated/0/levels/" + "level" + ".json");
        values = loadJSONArray(file);
      //} else {
      //  showToast("Please enable file reading permissions");
      //  requestPermission("android.permission.READ_EXTERNAL_STORAGE");
      //}

      for (int i = 0; i < values.size(); i++) {
        JSONObject platform = values.getJSONObject(i); 

        int pX = platform.getInt("pX");
        int pY = platform.getInt("pY");
        int pWidth = platform.getInt("pWidth");
        int pHeight = platform.getInt("pHeight");
        
        Platform p = new Platform(pX, pY, pWidth, pHeight);
        objects.add(p);
      }
    }
    catch(Exception e) {
      showToast(e.getMessage());
    }

    //return objects;
    game.world.clear();
    for(Rectangle r : objects){
      game.world.insert(r);
    }
  }
}
