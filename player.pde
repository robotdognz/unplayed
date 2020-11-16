class Player {
  private PVector position;
  private PVector velocity;
  private final int playerW = 100;
  private final int playerH = 100;
  private color playerColor;
  private int jumpCount = 0;
  private final int playerSpeed = 10;
  private final int playerGravity = 2;
  private final int terminalVelocity = 50;
  private final int playerJumpPower = 30;
  private boolean left = false;
  private boolean right = false;
  private PImage player;
  
  //vibration
  private Vibe vibe;
  private int vibration = 0; //y vibration amount(milliseconds)
  private boolean wall = false; //colliding with wall
  private float vibeVelocity = 0; //extra vibration added on after max velocity
  private float lastXPos; //x position one step back
  private float lastLastXPos; //x position two steps back
  
  Player(int x, int y, Vibe v) {
    vibe = v;
    lastXPos = x-playerW/2;
    lastLastXPos = lastXPos;
    
    position = new PVector(x-playerW/2, y); //y-playerH/2
    velocity = new PVector(0, 0);
    playerColor = color(255, 94, 22);
    player = loadImage("player_main.png");
  }

  void jump() {
    if (jumpCount > 0) {
      jumpCount--;
      velocity.y = -playerJumpPower;
    }
  }

  void collision(PVector platformTopLeft, PVector platformBottomRight) {
    //if a collision is happening
    if (platformTopLeft.y < position.y+playerH+Math.max(velocity.y, 0) && platformBottomRight.y > position.y+Math.min(velocity.y,0) &&
      platformTopLeft.x < position.x+playerW+velocity.x && platformBottomRight.x > position.x+velocity.x) {
      if (platformBottomRight.y < position.y+playerH/100-Math.min(velocity.y,0) //position.y+playerH/100-Math.min(velocity.y,0)
      && platformTopLeft.x < position.x+playerW && platformBottomRight.x > position.x) {
        //player is under
        if(velocity.y < 0){
          vibration = (int) Math.max((Math.exp(Math.abs(velocity.y/13))/5), 1); //8
        }
        position.y = platformBottomRight.y;
        velocity.y = 0;
      } else if (platformTopLeft.y > position.y+(playerH/20)*19-Math.min(velocity.y, 0)) { //+(playerH/20)*19
        //player is above
        if(velocity.y > 0){ 
          vibration = (int) Math.max((Math.exp((velocity.y+vibeVelocity)/15)/1.7), 1); //(Math.exp((velocity.y+vibeVelocity)/15)/1.7))
        }
        position.y = platformTopLeft.y-playerH;
        velocity.y = 0;
        jumpCount = 2;
      } else if (platformTopLeft.x > position.x+(playerW/3)*2) { //+(playerW/3)*2
        //player is to the left
        position.x = platformTopLeft.x-playerW;
        velocity.x = 0;
        wall = true;
      } else if (platformBottomRight.x < position.x+playerW/3) { //+playerW/3
        //player is to the right
        position.x = platformBottomRight.x;
        velocity.x = 0;
        wall = true;
      } else {
        //fringe case where the player would fall through
        //aka player is in a weird place
        position.y = platformTopLeft.y-playerH;
        velocity.y = 0;
      }
    }
  }

  void step(ArrayList<Platform> platforms, ArrayList<Event> events, Game g) {
    float previousY = position.y;
    vibration = 0;
    if(velocity.y < terminalVelocity){
      //limit fall speed by terminalVelocity
      velocity.y += playerGravity;
      vibeVelocity = 0;
    }else if (velocity.y+playerGravity > terminalVelocity){
      //fall speed exactyly terminalVelocity
      velocity.y = terminalVelocity;
      vibeVelocity += playerGravity/2;
    }
    position.y += velocity.y; //this comes before collision so that falling through perfect holes works
    velocity.x = 0;
    
    if (left) {
      velocity.x = -playerSpeed;
    }
    if (right) {
      velocity.x = playerSpeed;
    }
    
    //do collision
    wall = false;
    for (Platform p : platforms) {
      collision(p.getTopLeft(), p.getBottomRight());
    }
    
    position.x += velocity.x;
    
    //ground and roof vibration
    if(position.y != previousY && vibration > 0){
      //vibe.vibrate(VibrationEffect.createOneShot(vibration, 255));
      vibe.vibrate(vibration);
    }
    //wall vibration
    if(wall && lastLastXPos != position.x){
      //vibe.vibrate(VibrationEffect.createOneShot(1, 160));
      vibe.vibrate(1, 160);
    }
    
    //event collision
    for (Event e: events){
      PVector eventTopLeft = e.getTopLeft();
      PVector eventBottomRight = e.getBottomRight();
      //if colliding with the event
      if (eventTopLeft.y < position.y+playerH+velocity.y && eventBottomRight.y > position.y+velocity.y &&
      eventTopLeft.x < position.x+playerW && eventBottomRight.x > position.x) {
        e.activate(g);
      }
    }
    
    //stores previous positions for wall vibration
    lastLastXPos = lastXPos;
    lastXPos = position.x;
  }

  void draw() {
    //draw player
    fill(playerColor);
    noStroke();
    //rect(position.x, position.y, playerW, playerH);
    image(player, position.x, position.y, playerW, playerH);
  }
  
  public void drawArrows(Game g){
    //draw player-off-screen arrows
    if (position.x+playerW-10 <= g.leftEdge) {
      //left edge
      triangle(g.leftEdge+20, position.y+playerH/2, g.leftEdge+60, position.y+playerH/2-40, g.leftEdge+60, position.y+playerH/2+40);
    }
    if (position.x+10 >= g.rightEdge) {
      //right edge
      triangle(g.rightEdge-20, position.y+playerH/2, g.rightEdge-60, position.y+playerH/2-40, g.rightEdge-60, position.y+playerH/2+40);
    }
    if (position.y+playerH-10 <= g.topEdge){
      //top edge
      triangle(position.x+playerW/2, g.topEdge+20, position.x+40+playerW/2, g.topEdge+60, position.x-40+playerW/2, g.topEdge+60);
    }
    if (position.y+10 >= g.bottomEdge){
      //top edge
      triangle(position.x+playerW/2, g.bottomEdge-20, position.x+40+playerW/2, g.bottomEdge-60, position.x-40+playerW/2, g.bottomEdge-60);
    }
    //need to add corner arrows
  }
  
  public void setPosition(PVector newPosition){
    position.x = newPosition.x-playerW/2;
    position.y = newPosition.y;
  }

  void left() {
    left = true;
    right = false;
  }
  void right() {
    left = false;
    right = true;
  }
  void still() {
    left = false;
    right = false;
  }
}
