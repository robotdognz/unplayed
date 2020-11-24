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

      platform.setFloat("xPos", r.getX());
      platform.setFloat("yPos", r.getY());
      platform.setFloat("width", r.getWidth());
      platform.setFloat("height", r.getHeight());

      values.setJSONObject(values.size(), platform); //add it on to the end
    }
    
    
    
    //String location = dataPath("") + '/';
    //String path = "/storage/emulated/0/levels/";
    //File saveDir = new File(dataPath("")+'/');
    
    //File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    //File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    File file = new File(context.getFilesDir(), "level.json");

    saveJSONArray(values, file.getAbsolutePath()); //path + "level.json"
  }

  public void load() {
    //loadJSONArray()
  }
}
