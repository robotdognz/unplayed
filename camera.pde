//so far none of this makes use of being an absract class, would be more efficent to use a single static class
//that has methods for changing the 'game' variable. Also change the 'game' variable name to something more 
//logical and easy to understand when reading the code

//------------------AbstractCamera---------------------
static abstract class Camera{
  static protected boolean game = true; //is the game class allowed to make changes to the camera
  
  //variables for camera
  static protected float scale;
  static protected float subScale = 1; //defaults to 1
  static protected PVector center;
  
  public boolean getGame(){
    return game;
  }
  
  public float getScale(){
    return scale;
  }
  
  public void setScale(float newScale){
    scale = newScale;
  }
  
  public float getSubScale(){
    return subScale;
  }
  
  public void setSubScale(float newSubScale){
    subScale = newSubScale;
  }
  
  public PVector getCenter(){
    return center;
  }
  
  public void setCenter(PVector newCenter){
     center.x = newCenter.x; 
     center.y = newCenter.y;
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
