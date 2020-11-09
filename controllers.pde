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
  public void touchEnded() {
  }
  public void touchMoved() {
  }
  public void onPinch(float x, float y, float d) {
  }
}

//------------------CameraController---------------------
class CameraControl implements Controller {
  public void draw() {
  }
  public void touchStarted() {
  }
  public void touchEnded() {
  }
  public void touchMoved() {
    if (touches.length == 1) {
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
  public void draw() {
  }
  public void touchStarted() {
  }
  public void touchEnded() {
  }
  public void touchMoved() {
  }
  public void onPinch(float x, float y, float d) {
  }
}

//------------------BlankController---------------------
class BlankControl implements Controller {
  public void draw() {
  }
  public void touchStarted() {
  }
  public void touchEnded() {
  }
  public void touchMoved() {
  }
  public void onPinch(float x, float y, float d) {
  }
}
