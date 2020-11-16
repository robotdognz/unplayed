//------------------ControllerInterface---------------------
interface Controller {
  public void draw();
  public void touchStarted();
  public void touchEnded();
  public void touchMoved();
  public void onPinch(float x, float y, float d);
}

//------------------GameController---------------------
class PlayerControl implements Controller {
  public void draw() {
    int left = 0;
    int right = 0;
    for (TouchEvent.Pointer t : touches) {
      if (t.y >=  height/3) {
        if (t.x < width/4) {
          left++;
        }
        if (t.x > (width/4)*3) {
          right++;
        }
      }
    }
    if (left > right) {
      g.player.left();
    } else if (left < right) {
      g.player.right();
    } else {
      g.player.still();
    }
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
  Editor editor;
  
  public CameraControl(Editor editor){
    this.editor = editor;
  }
  
  public void draw() {
  }
  public void touchStarted() {
  }
  public void touchEnded() {
  }
  public void touchMoved() {
    if (touches.length == 1) {
      if (mouseY < 180) { //don't respond to clicks at the top
        return;
      }
      float moveX = pmouseX - mouseX;
      float moveY = pmouseY - mouseY;
      PVector diff = new PVector(moveX, moveY);
      gCamera.setCenter(gCamera.getCenter().add(diff));
    }
  }
  public void onPinch(float x, float y, float d) {
    if (touches.length == 2) {
      float newScale = gCamera.getScale()-d;
      if (newScale < editor.minZoom) {
        newScale = editor.minZoom;
      }
      if (newScale > editor.maxZoom) {
        newScale = editor.maxZoom;
      }
      gCamera.setScale(newScale);
    }
  }
}

//------------------EditorController---------------------
class EditorControl implements Controller {
  Editor editor;
  
  public EditorControl(Editor editor){
    this.editor = editor;
  }
  
  public void draw() {
  }
  public void touchStarted() {
  }
  public void touchEnded() {
  }
  public void touchMoved() {
    if (mouseY < 180) { //don't respond to clicks at the top
      return;
    }
    
    float snapNo = 10;
    if(editor.snap){
      snapNo = 100;
    }
    
    //calculate position in level
    float currentScale = gCamera.getScale();
    float currentSubScale = gCamera.getSubScale();
    PVector currentCenter = gCamera.getCenter();
    float levelX = ((mouseX-width/2)/((float)width/currentScale)/currentSubScale) + currentCenter.x;
    float levelY = ((mouseY-height/2)/((float)width/currentScale)/currentSubScale) + currentCenter.y;

    //round so blocks snap to grid
    float finalX = Math.round(levelX/snapNo)*snapNo;
    float finalY = Math.round(levelY/snapNo)*snapNo;

    g.point = new PVector(finalX, finalY);
  }
  public void onPinch(float x, float y, float d) {
  }
}
