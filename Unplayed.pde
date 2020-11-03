Game g;

void setup() {
  fullScreen(OPENGL);
  frameRate(60);
  g = new Game();
}

void draw() {
  g.draw();
}
