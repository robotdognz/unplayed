//------------------AbstractWidget---------------------
abstract class Widget{
  protected PVector position = new PVector(120,120);
  protected float pWidth = 60;
  protected float pHeight = 60;
  String folder = dataPath("ui")+'/';
  protected PImage off;
  protected PImage on;
  protected boolean hover = false;
  protected boolean active = false;
  
  public void hover(PVector lastTouch){
    if (lastTouch.x >= position.x-pWidth*1.5 && 
    lastTouch.y >= position.y-pHeight*1.5 && 
    lastTouch.x <= position.x+pWidth*1.5 && 
    lastTouch.y <= position.y+pHeight*1.5) {
      hover = true;
    }else{
      hover = false;
    }
  }
  
  public void draw(float wX, float wY){
    position.x = wX;
    position.y = wY;
    imageMode(CENTER);
    if(active){
      image(on, position.x, position.y, pWidth, pHeight);
    }else{
      image(off, position.x, position.y, pWidth, pHeight);
    }
    imageMode(CORNER);
  }
  
  public void click(){}
  
  public void active(){}
}

//------------------Pause---------------------
class PauseWidget extends Widget{
  boolean previousStatus = false;
  
  public PauseWidget(){
    on = loadImage(folder+"PauseClick.png");
    off = loadImage(folder+"Pause.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        active = true;
        previousStatus = gPaused;
        gPaused = true; //switch pause state
        menu = new PauseMenu(this);
        //con = new BlankControl();
      }
    }
  }
}

//------------------Player---------------------
class PlayerWidget extends Widget{
  public PlayerWidget(){
    on = loadImage(folder+"playerControlsClick.png");
    off = loadImage(folder+"playerControls.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        active = true;
        gPaused = true;
        con = new CameraControl();
      }else{
        active = false;
        gPaused = false;
        con = new GameControl();
      }
    }
  }
}

//------------------Camera---------------------
class CameraWidget extends Widget{
  public CameraWidget(){
    on = loadImage(folder+"CameraControlsClick.png");
    off = loadImage(folder+"CameraControls.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        active = true;
        c = new FreeCamera();
      }else{
        active = false;
        c = new GameCamera();
      }
    }
  }
}

//------------------Block---------------------
class BlockWidget extends Widget{
  public BlockWidget(){
    on = loadImage(folder+"PlaceBlockClick.png");
    off = loadImage(folder+"PlaceBlock.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        active = true;
        con = new BlockControl();
      }else{
        active = false;
        con = new GameControl();
      }
    }
  }
}
