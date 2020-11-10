//------------------AbstractWidget---------------------
abstract class Widget{
  protected PVector position;
  protected float pWidth, pHeight;
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
  
  public void draw(){}
  public void click(){}
}

//------------------Pause---------------------
class PauseWidget extends Widget{
  
  public PauseWidget(){
    position = new PVector(120,120);
    pWidth = 60;
    pHeight = 60;
    on = loadImage("ui/PauseClick.png");
    off = loadImage("ui/Pause.png");
  }
  
  public void draw(){
    if(menu == null || !(menu instanceof PauseMenu)){ //if pause menu is closed, draw the widget
      fill(150);
      //ellipseMode(CENTER);
      //ellipse(position.x, position.y, pWidth, pHeight);
      //ellipseMode(CORNER);
      imageMode(CENTER);
      if(active){
        image(on, position.x, position.y, pWidth, pHeight);
      }else{
        image(off, position.x, position.y, pWidth, pHeight);
      }
      imageMode(CORNER);
    }
  }
  
  public void click(){
    if(hover){
      gPaused = true; //switch pause state
      menu = new PauseMenu();
      con = new BlankControl();
    }
  }
}

//------------------Player---------------------
class PlayerWidget extends Widget{
  public PlayerWidget(){
    on = loadImage("ui/playerControlsClick.png");
    off = loadImage("ui/playerControls.png");
  }
}

//------------------Camera---------------------
class CameraWidget extends Widget{
  public CameraWidget(){
    on = loadImage("ui/CameraControlsClick.png");
    off = loadImage("ui/CameraControls.png");
  }
}

//------------------Block---------------------
class BlockWidget extends Widget{
  public BlockWidget(){
    on = loadImage("ui/PlaceBlockClick.png");
    off = loadImage("ui/PlaceBlock.png");
  }
}
