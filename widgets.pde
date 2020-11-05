//------------------Pause---------------------
class Pause implements Widget{
  PVector position;
  float pWidth, pHeight;
  PVector bottomRight;
  boolean hover = false;
  
  public Pause(){
    position = new PVector(120,120);
    pWidth = 60;
    pHeight = 60;
    bottomRight = new PVector(position.x+pWidth, position.y+pHeight);
  }
  
  public void draw(){
    if(!gPaused){
      fill(150);
    }else{
      fill(150,0,0,0);
    }
    ellipseMode(CENTER);
    
   //if(hover){
   // ellipse(position.x, position.y, pWidth*3, pHeight*3);
   //}else{
      ellipse(position.x, position.y, pWidth, pHeight);
   //}
    ellipseMode(CORNER);
  }
  
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
  
  public void click(){
    if(hover){
      gPaused = true; //switch pause state
      menu = new PauseMenu();
    }
  }
}
