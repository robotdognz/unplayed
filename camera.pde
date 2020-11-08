//------------------AbstractCamera---------------------
static abstract class Camera{
  static protected boolean game = true; //is the game class allowed to make changes to the camera
  
  //variables for camera
  static protected float scale;
  static public float newScale;
  static protected PVector center;
  static public PVector newCenter;
  static public float zoomSpeed = 0.1; //0.1 is the default

  //variables for camera tall screen space
  static protected float subScale = 1; //1 is the default
  static protected float newSubScale = subScale;

  //variables for black border
  static protected float topEdge;
  static public float newTopEdge;
  static protected float bottomEdge;
  static public float newBottomEdge;
  static protected float leftEdge;
  static public float newLeftEdge;
  static protected float rightEdge;
  static public float newRightEdge;
  static public float boarderZoomSpeed = 0.1; //0.1 is default
  
  public boolean getGame(){
    return game;
  }
  
  public static class values{
    
  }
  
}

//------------------GameCamera---------------------
class GameCamera extends Camera{
  GameCamera(){
    game = true;
  }
  
  
}

//------------------FreeCamera---------------------
class FreeCamera extends Camera{
  FreeCamera(){
   game = false; 
  }
  
  
}
