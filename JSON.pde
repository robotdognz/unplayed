class EditorJSON {
  WriteObjectFile writer;
  JSONArray values;

  public EditorJSON() {
    writer = new WriteObjectFile(context);
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
    //File file = new File("level.json"); //context.getFilesDir(), 

    // writer.writeObject(values.toString(), "level.json");

    // Convert JsonObject to String Format
    //try {
    //  String userString = values.toString();// Define the File Path and its Name
    //  File file = new File(context.getFilesDir(), "level.json");
    //  FileWriter fileWriter = new FileWriter(file);
    //  BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    //  bufferedWriter.write(userString);
    //  bufferedWriter.close();
    //}
    //catch (IOException e) {
    //  e.printStackTrace();
    //}

    try {
      Writer output = null;
      //File dir = context.getFilesDir();
      File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
      //File file = new File("storage/emulated/0/" + "level" + ".json");
      File file = new File(dir + "level" + ".json");
      output = new BufferedWriter(new FileWriter(file));
      output.write(values.toString());
      output.close();
      //Toast.makeText(context, "Composition saved", Toast.LENGTH_LONG).show(); //getApplicationContext()
      showToast("Composition saved");
    } 
    catch (Exception e) {
      //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show(); //getBaseContext()
      showToast(e.getMessage());
    }

    //saveJSONArray(values, file.getAbsolutePath()); //path + "level.json"

    //String filename = "myfile";
    //String fileContents = "Hello world!";
    //try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
    //  fos.write(fileContents.toByteArray());
    //}
  }


  public void load() {
    //loadJSONArray()
  }
}

public class WriteObjectFile {

  private Context parent;
  private FileInputStream fileIn;
  private FileOutputStream fileOut;
  private ObjectInputStream objectIn;
  private ObjectOutputStream objectOut;
  private Object outputObject;
  private String filePath;

  public WriteObjectFile(Context c) {
    parent = c;
  }

  public Object readObject(String fileName) {
    try {
      filePath = parent.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
      fileIn = new FileInputStream(filePath);
      objectIn = new ObjectInputStream(fileIn);
      outputObject = objectIn.readObject();
    } 
    catch (FileNotFoundException e) {
      e.printStackTrace();
    } 
    catch (IOException e) {
      e.printStackTrace();
    } 
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    } 
    finally {
      if (objectIn != null) {
        try {
          objectIn.close();
        } 
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return outputObject;
  }

  public void writeObject(Object inputObject, String fileName) {
    try {
      filePath = parent.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
      fileOut = new FileOutputStream(filePath);
      objectOut = new ObjectOutputStream(fileOut);
      objectOut.writeObject(inputObject);
      fileOut.getFD().sync();
    } 
    catch (IOException e) {
      e.printStackTrace();
    } 
    finally {
      if (objectOut != null) {
        try {
          objectOut.close();
        } 
        catch (IOException e) { 
          e.printStackTrace();
        }
      }
    }
  }
}
