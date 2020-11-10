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

class MenuWidget extends Widget{
  boolean previousStatus = false;
  
  public MenuWidget(){
    off = loadImage(folder+"menuClick.png");
    on = loadImage(folder+"menu.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        active = true;
        previousStatus = gPaused;
        gPaused = true; //switch pause state
        menu = new PauseMenu(this);
      }
    }
  }
}

//------------------Pause---------------------
class PauseWidget extends Widget{
  boolean previousStatus = false;
  
  public PauseWidget(){
    off = loadImage(folder+"PauseClick.png");
    on = loadImage(folder+"Pause.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        c = new FreeCamera();
      }else{
        c = new GameCamera();
      }
    }
  }
  
  public void active(){
    if(c instanceof FreeCamera){
      active = true;
    }else{
      active = false;
    }
  }
}

//------------------PlayerControls---------------------
class PlayerWidget extends Widget{
  public PlayerWidget(){
    off = loadImage(folder+"playerControlsClick.png");
    on = loadImage(folder+"playerControls.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        con = new GameControl();
      }
    }
  }
  
  public void active(){
    if(con instanceof GameControl){
      active = true;
    }else{
      active = false;
    }
  }
}

//------------------CameraControls---------------------
class CameraWidget extends Widget{
  public CameraWidget(){
    off = loadImage(folder+"CameraControlsClick.png");
    on = loadImage(folder+"CameraControls.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        con = new CameraControl();
      }
    }
  }
  
  public void active(){
    if(con instanceof CameraControl){
      active = true;
    }else{
      active = false;
    }
  }
}

//------------------Block---------------------
class BlockWidget extends Widget{
  public BlockWidget(){
    off = loadImage(folder+"PlaceBlockClick.png");
    on = loadImage(folder+"PlaceBlock.png");
  }
  
  public void click(){
    if(hover){
      if(!active){
        con = new BlockControl();
      }
    }
  }
  
  public void active(){
    if(con instanceof BlockControl){
      active = true;
    }else{
      active = false;
    }
  }
}
