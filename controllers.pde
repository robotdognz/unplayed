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
  int maxZoomSpeed;

  public CameraControl(Editor editor) {
    this.editor = editor;
    maxZoomSpeed = 200;
  }

  public void draw() {
  }
  public void touchStarted() {
  }
  public void touchEnded() {
  }
  public void touchMoved() {
    if (touches.length == 1) {
      if (mouseY < 200) { //don't respond to clicks at the top
        return;
      }
      float moveX = (pmouseX - mouseX)/3;
      float moveY = (pmouseY - mouseY)/3;
      PVector diff = new PVector(convert.screenToLevel(moveX), convert.screenToLevel(moveY));
      gCamera.setCenter(gCamera.getCenter().add(diff));
    }
  }
  public void onPinch(float x, float y, float d) {
    if(d > maxZoomSpeed){
      d = maxZoomSpeed;
    }
    if(d < -maxZoomSpeed){
      d = -maxZoomSpeed;
    }
    
    if (touches.length == 2) {
      float newScale = gCamera.getScale()-convert.screenToLevel(d);
      float newTotalScale = convert.getTotalFromScale(newScale);
      if (newTotalScale < editor.minZoom) {
        newScale = convert.getScaleFromTotal(editor.minZoom);//editor.minZoom;
      }
      if (newTotalScale > editor.maxZoom) { 
        newScale = convert.getScaleFromTotal(editor.maxZoom);//editor.maxZoom;
      }
      gCamera.setScale(newScale);
    }
  }
}

//------------------EditorController---------------------
class EditorControl implements Controller {
  Editor editor;

  public EditorControl(Editor editor) {
    this.editor = editor;
  }

  public void draw() {
  }
  public void touchStarted() {
  }
  public void touchEnded() {
  }
  public void touchMoved() {
    if (mouseY < 200) { //don't respond to clicks at the top
      return;
    }

    float snapNo = 10;
    if (editor.snap) {
      snapNo = 100;
    }

    //calculate position in level
    PVector placement = convert.screenToLevel(mouseX, mouseY);
    ////round so blocks snap to grid
    float finalX = Math.round((placement.x-50)/snapNo)*snapNo;
    float finalY = Math.round((placement.y-50)/snapNo)*snapNo;

    g.point = new PVector(finalX, finalY);
    if (editor.snap) {
      editor.placeBlock();
    }
  }

  public void onPinch(float x, float y, float d) {
  }
}
