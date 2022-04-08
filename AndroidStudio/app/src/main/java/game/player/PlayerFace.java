package game.player;

import game.AppLogic;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import static processing.core.PConstants.*;

public class PlayerFace {
    private PlayerState state;
    private final PImage defaultFace;

    private PImage sleepingFace;

    private PImage transition;

    public PlayerFace(PApplet p) {
        // initialise the player state
        state = PlayerState.DEFAULT;

        // load in the face sprites
        String folder = "player" + '/'; // data path for player face sprites
        defaultFace = p.loadImage(folder + "PF_Default.png");
        transition = p.loadImage(folder + "Transition.png");

    }

    /**
     * Draws the player face based on the state the active player is currently in
     *
     * @param g      graphics pane to draw the player face on
     * @param width  width to draw the face
     * @param height height to draw the face
     */
    public void draw(PGraphics g, float width, float height) {
        // draw the face
        g.imageMode(CENTER);
        switch (state) {
            case DEFAULT:
                g.image(defaultFace, 0, 0, width, height);
                break;
            case DEAD:
                break;

        }
    }

    public void drawTransition(PGraphics g, float x, float y, float width, float height) {
        g.imageMode(CENTER);
        g.image(transition, x, y, width, height);
    }

    public void drawAll(PGraphics g) {
        g.image(defaultFace, 0, 0, 100, 100);
    }

    /**
     * Draws the player sleeping face. This is used by tiles placed by the player or
     * tiles that will eventually become the player.
     *
     * @param g      graphics pane to draw the player face on
     * @param width  width to draw the face
     * @param height height to draw the face
     */
    public void drawAsleep(PGraphics g, float width, float height) {
        g.imageMode(CENTER);
        g.image(sleepingFace, 0, 0, width, height);
    }

    /**
     * Update the player state stored inside PlayerFace. This will be used to
     * determine what face to draw on the player
     *
     * @param newState the new state the player will be drawn with
     */
    public void setState(PlayerState newState) {
        this.state = newState;
    }

    public enum PlayerState {
        DEFAULT,
        MOVING, // moving on the ground
        AIR_JUMP, // in the air and can do a double jump
        AIR_NO_JUMP, // in the air and can't do a double jump
        DEAD,
    }
}
