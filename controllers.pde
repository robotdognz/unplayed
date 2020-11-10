//------------------ControllerInterface---------------------
interface Controller {
  public void draw();
  public void touchStarted();
  public void touchEnded();
  public void touchMoved();
  public void onPinch(float x, float y, float d);
}

//------------------GameController---------------------
class GameControl implements Controller {
  public void draw() {
    playerDirection(); //update player left right controls
  }
  public void touchStarted() {
    //jump if the last true touch was in the middle of the screen
    if (lastTouch.y >= height/3 && 
    lastTouch.x > width/4 && 
    lastTouch.x < (width/4)*3) {
      g.player.jump();
    }
  }
  public void touchEnded() {}
  public void touchMoved() {}
  public void onPinch(float x, float y, float d) {}
}

//------------------CameraController---------------------
class CameraControl implements Controller {
  public void draw() {}
  public void touchStarted() {}
  public void touchEnded() {}
  public void touchMoved() {
    if (touches.length == 1) {
      if(mouseY < 180){ //don't respond to clicks at the top
        return;
      }
      float moveX = pmouseX - mouseX;
      float moveY = pmouseY - mouseY;
      PVector diff = new PVector(moveX, moveY);
      c.setCenter(c.getCenter().add(diff));
    }
  }
  public void onPinch(float x, float y, float d) {
    if (touches.length == 2) {
      float newScale = c.getScale()-d;
      if (newScale < minZoom) {
        newScale = minZoom;
      }
      if (newScale > maxZoom) {
        newScale = maxZoom;
      }
      c.setScale(newScale);
    }
  }
}

//------------------BlockPlacementController---------------------
class BlockControl implements Controller {
  public void draw() {}
  public void touchStarted() {}
  public void touchEnded() {}
  public void touchMoved() {
    if(mouseY < 180){ //don't respond to clicks at the top
      return;
    }
    
    //draw pointer
    fill(0,0,0,150);
    rectMode(CENTER);
    rect(mouseX, mouseY, 100, 100);
    rectMode(CORNER);
    textSize(30);
      textAlign(LEFT, CENTER);
      String s = "[ " + mouseX + ", " + mouseY + " ]";
      text(s, mouseX+55, mouseY);
    
    float blockX = mouseX-width/2;
    //float blockX = Math.round((mouseX-width/2)/10.0)*10;
    float blockY = mouseY;
    //float blockY = Math.round(mouseY/10.0)*10;
    
    float finalX = Math.round(blockX/10.0)*10;
    float finalY = Math.round(blockY/10.0)*10;
    g.point = new PVector(finalX, finalY);
  }
  public void onPinch(float x, float y, float d) {}
}
