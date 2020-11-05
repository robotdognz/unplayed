//------------------PauseMenu---------------------
abstract class AbstractMenu {
  ArrayList<Button> buttons = new ArrayList<Button>();
  
  public void draw() {
    fill(150);
    rectMode(CORNERS);
    rect(200, 800, width-200, height-800);
    rectMode(CORNER);
    for (Button b : buttons) {
      b.draw();
    }
  }
  
  public void hover(PVector lastTouch) {
    for (Button b : buttons) {
      b.hover(lastTouch);
    }
  }
  
  public void click() {
    
  }
}

class PauseMenu extends AbstractMenu{
  String resume = "Resume";
  
  public PauseMenu() {
    Button r = new Button(new PVector(width/2, 1000), 500, 200, resume);
    buttons.add(r);
  }
  
  public void click() {
    for (Button b : buttons) {
      if (b.click().equals(resume)) { //resume the game if resume button pressed
        gPaused = false;
        menu = null;
      }
    }
  }
}


//abstract class might work better for menus
//might be able to bake in drawing code
//class PauseMenu implements Menu {
//  ArrayList<Button> buttons = new ArrayList<Button>();
//  String resume = "Resume";

//  public PauseMenu() {
//    Button r = new Button(new PVector(width/2, 1000), 500, 200, resume);
//    buttons.add(r);
//  }

//  public void draw() {
//    fill(150);
//    rectMode(CORNERS);
//    rect(200, 800, width-200, height-800);
//    rectMode(CORNER);
//    for (Button b : buttons) {
//      b.draw();
//    }
//  }

//  public void hover(PVector lastTouch) {
//    for (Button b : buttons) {
//      b.hover(lastTouch);
//    }
//  }

//  public void click() {
//    for (Button b : buttons) {
//      if (b.click().equals(resume)) { //resume the game if resume button pressed
//        gPaused = false;
//        menu = null; //this is problematic
//      }
//    }
//  }
//}

//------------------Button---------------------
class Button {
  private PVector bCenter;
  private float bWidth, bHeight;
  private String text;
  private boolean hover = false;

  public Button(PVector bCenter, float bWidth, float bHeight, String text) {
    this.bCenter = bCenter;
    this.bWidth = bWidth;
    this.bHeight = bHeight;
    this.text = text;
  }

  public void draw() {
    //can use textWidth() to figure out how wide text is and center it
    if(!hover){
      fill(200);
    }else{
      fill(100);
    }
    rectMode(CENTER);
    rect(bCenter.x, bCenter.y, bWidth, bHeight);
    rectMode(CORNER);
    fill(50);
    textSize(60);
    textAlign(CENTER, CENTER);
    text(text, bCenter.x, bCenter.y);
    //text(text, bCenter.x-bWidth/2, bCenter.y-bHeight/2, bCenter.x+bWidth/2, bCenter.y+bHeight/2);
  }

  public String click() {
    if (hover) {
      return text;
    } else {
      return "";
    }
  }

  public void hover(PVector lastTouch) {
    if (lastTouch.x >= bCenter.x-bWidth/2 && 
      lastTouch.y >= bCenter.y-bHeight/2 && 
      lastTouch.x <= bCenter.x+bWidth/2 && 
      lastTouch.y <= bCenter.y+bHeight/2) {
      hover = true;
    } else {
      hover = false;
    }
  }
}
