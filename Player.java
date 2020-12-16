package game;

import java.util.HashSet;

import handlers.TextureCache;
import misc.Vibe;
import objects.Event;
import objects.Rectangle;
import objects.Tile;
import processing.core.*;
import static processing.core.PConstants.*;

public class Player extends Rectangle {
	private PApplet p;
	private PVector previousPosition;

	// player fields
	private Rectangle playerArea; // rectangle used for searching the level quad tree
	private int areaSize;
	private boolean drawArea = false;
	private PVector velocity;
	private int playerColor; // TODO: switched from lower case c to upper case c java.awt.color, might not
								// work in processing
	private int jumpCount = 0;
	private final int playerSpeed = 10;
	private final int playerGravity = 2;
	private final int terminalVelocity = 50;
	private final int playerJumpPower = 30;
	private boolean left = false;
	private boolean right = false;
	private PImage sprite;

	// vibration
	private Vibe vibe;
	private int vibration = 0; // y vibration amount(milliseconds)
	private boolean wall = false; // colliding with wall
	private float vibeVelocity = 0; // extra vibration added on after max velocity
	private float lastXPos; // x position one step back
	private float lastLastXPos; // x position two steps back

	Player(PApplet p, TextureCache texture, float x, float y, Vibe v) {
		super(x, y, 100, 100);
		this.p = p;
		areaSize = 500;

		playerArea = new Rectangle(getX() - ((areaSize - 100) / 2), getY() - ((areaSize - 100) / 2), areaSize,
				areaSize);

		vibe = v;
		lastXPos = x;
		lastLastXPos = lastXPos;

		velocity = new PVector(0, 0);
		playerColor = p.color(255, 94, 22);
		sprite = texture.defaultBlock;
		previousPosition = new PVector(getX(), getY());
	}

	public void jump() {
		if (jumpCount > 0) {
			jumpCount--;
			velocity.y = -playerJumpPower;
		}
	}

	void collision(PVector platformTopLeft, PVector platformBottomRight) {
		// if a collision is happening
		if (platformTopLeft.y < getTopLeft().y + getHeight() + Math.max(velocity.y, 0)
				&& platformBottomRight.y > getTopLeft().y + Math.min(velocity.y, 0)
				&& platformTopLeft.x < getTopLeft().x + getWidth() + velocity.x
				&& platformBottomRight.x > getTopLeft().x + velocity.x) {
			if (platformBottomRight.y < getTopLeft().y + getHeight() / 100 - Math.min(velocity.y, 0) // position.y+playerH/100-Math.min(velocity.y,0)
					&& platformTopLeft.x < getTopLeft().x + getWidth() && platformBottomRight.x > getTopLeft().x) {
				// player is under
				if (velocity.y < 0) {
					vibration = (int) Math.max((Math.exp(Math.abs(velocity.y / 13)) / 5), 1); // 8
				}
				setY(platformBottomRight.y);
				velocity.y = 0;
			} else if (platformTopLeft.y > getTopLeft().y + (getHeight() / 20) * 19 - Math.min(velocity.y, 0)) { // +(playerH/20)*19
				// player is above
				if (velocity.y > 0) {
					vibration = (int) Math.max((Math.exp((velocity.y + vibeVelocity) / 15) / 1.7), 1); // (Math.exp((velocity.y+vibeVelocity)/15)/1.7))
				}
				setY(platformTopLeft.y - getHeight());
				velocity.y = 0;
				jumpCount = 2;
			} else if (platformTopLeft.x > getTopLeft().x + (getWidth() / 3) * 2) { // +(playerW/3)*2
				// player is to the left
				setX(platformTopLeft.x - getWidth());
				velocity.x = 0;
				wall = true;
			} else if (platformBottomRight.x < getTopLeft().x + getWidth() / 3) { // +playerW/3
				// player is to the right
				setX(platformBottomRight.x);
				velocity.x = 0;
				wall = true;
			} else {
				// fringe case where the player would fall through
				// aka player is in a weird place
				setY(platformTopLeft.y - getHeight());
				velocity.y = 0;
			}
		}
	}

	void step(HashSet<Rectangle> objects, Game g) {
		// store previous position, used for player end
		previousPosition.x = getX();
		previousPosition.y = getY();

		// code starts here
		float previousY = getTopLeft().y;
		vibration = 0;
		if (velocity.y < terminalVelocity) {
			// limit fall speed by terminalVelocity
			velocity.y += playerGravity;
			vibeVelocity = 0;
		} else if (velocity.y + playerGravity > terminalVelocity) {
			// fall speed exactyly terminalVelocity
			velocity.y = terminalVelocity;
			vibeVelocity += playerGravity / 2;
		}
		setY(getY() + velocity.y); // this comes before collision so that falling through perfect holes works
		velocity.x = 0;

		if (left) {
			velocity.x = -playerSpeed;
		}
		if (right) {
			velocity.x = playerSpeed;
		}

		// do collision
		wall = false;
		for (Rectangle p : objects) {
			if (p instanceof Tile) { // platform collison
				collision(p.getTopLeft(), p.getBottomRight());
			} else if (p instanceof Event) { // event collision
//				PVector eventTopLeft = p.getTopLeft();
//				PVector eventBottomRight = p.getBottomRight();
//				// if colliding with the event
//				if (eventTopLeft.y < getTopLeft().y + getHeight() + velocity.y
//						&& eventBottomRight.y > getTopLeft().y + velocity.y
//						&& eventTopLeft.x < getTopLeft().x + getWidth() && eventBottomRight.x > getTopLeft().x) {
//					((Event) p).activate(g);
//				}

				if (getTopLeft().x > p.getBottomRight().x) {
					continue;
				}
				if (getBottomRight().x < p.getTopLeft().x) {
					continue;
				}
				if (getTopLeft().y > p.getBottomRight().y) {
					continue;
				}
				if (getBottomRight().y < p.getTopLeft().y) {
					continue;
				}
				((Event) p).activate(g);
			}
		}

		setX(getX() + velocity.x);

		// ground and roof vibration
		if (getTopLeft().y != previousY && vibration > 0) {
			vibe.vibrate(vibration);
		}
		// wall vibration
		if (wall && lastLastXPos != getTopLeft().x) {
			vibe.vibrate(1, 160);
		}

		// stores previous positions for wall vibration
		lastLastXPos = lastXPos;
		lastXPos = getTopLeft().x;
	}

	public PVector getVelocity() {
		return velocity;
	}

	public boolean isStill() {
		if (previousPosition.x != getX()) {
			return false;
		}
		if (previousPosition.y != getY()) {
			return false;
		}
		return true;
	}

	public void draw(PGraphics graphics) {
		// draw player
		graphics.imageMode(CORNER);
		graphics.image(sprite, getTopLeft().x, getTopLeft().y, getWidth(), getHeight());
		// image(img, dx, dy, dw, dh, sx, sy, sw, sh); //d is where to draw it, s is
		// where (in pixels) to get it from the image
		// image(sprite, getTopLeft().x, getTopLeft().y, getWidth(), getHeight(), 0, 0,
		// 128, 128); //sprite sheet test
		if (drawArea) {
			graphics.fill(0, 0, 0, 150);
			graphics.rect(playerArea.getX(), playerArea.getY(), playerArea.getWidth(), playerArea.getHeight());
		}
	}

	public void drawArrows(Game g) {
		p.fill(playerColor);
		// draw player-off-screen arrows
//		if (getTopLeft().x + getWidth() - 10 <= g.leftEdge) {
//			// left edge
//			p.triangle(g.leftEdge + 20, getTopLeft().y + getHeight() / 2, g.leftEdge + 60,
//					getTopLeft().y + getHeight() / 2 - 40, g.leftEdge + 60, getTopLeft().y + getHeight() / 2 + 40);
//		}
//		if (getTopLeft().x + 10 >= g.rightEdge) {
//			// right edge
//			p.triangle(g.rightEdge - 20, getTopLeft().y + getHeight() / 2, g.rightEdge - 60,
//					getTopLeft().y + getHeight() / 2 - 40, g.rightEdge - 60, getTopLeft().y + getHeight() / 2 + 40);
//		}
//		if (getTopLeft().y + getHeight() - 10 <= g.topEdge) {
//			// top edge
//			p.triangle(getTopLeft().x + getWidth() / 2, g.topEdge + 20, getTopLeft().x + 40 + getWidth() / 2,
//					g.topEdge + 60, getTopLeft().x - 40 + getWidth() / 2, g.topEdge + 60);
//		}
//		if (getTopLeft().y + 10 >= g.bottomEdge) {
//			// top edge
//			p.triangle(getTopLeft().x + getWidth() / 2, g.bottomEdge - 20, getTopLeft().x + 40 + getWidth() / 2,
//					g.bottomEdge - 60, getTopLeft().x - 40 + getWidth() / 2, g.bottomEdge - 60);
//		}
		// need to add corner arrows
	}

	public Rectangle getPlayerArea() {
		playerArea.setX(getX() - (areaSize - 100) / 2);
		playerArea.setY(getY() - (areaSize - 100) / 2);
		return playerArea;
	}

	public void resetVelocity() {
		velocity.x = 0;
		velocity.y = 0;
	}

	public void left() {
		left = true;
		right = false;
	}

	public void right() {
		left = false;
		right = true;
	}

	public void still() {
		left = false;
		right = false;
	}
}