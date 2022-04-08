package game.player;

import static processing.core.PConstants.*;

import org.jbox2d.common.Vec2;

import game.AppLogic;
import objects.View;
import processing.core.PApplet;
import processing.core.PGraphics;

public class PlayerTransition {
    private float movementDuration; // duration of animation in seconds
    private final float normalSpeed; // the distance travelled per second for normal transitions
    private final float respawnSpeed; // the distance travelled per second for death transitions
    private Type type; // what type of transition is currently happening
    private boolean isActive = false; // true if a transition is currently happening

    // bezier curve coordinates
    private Vec2 p0;
    private Vec2 p1;
    private Vec2 p2;
    private Vec2 p3;

    private Vec2 point; // where the transition effect is currently
    private float position = 0; // where the transition is on its journey between 0 and 1
    private float positionEnd = 0; // early end for the transition, between 1 and 0

    private final float size; // the size of the transition effect

    public PlayerTransition() { //Vec2 start, Vec2 end
        point = new Vec2(0, 0);
        movementDuration = 1;
        size = 50;

        normalSpeed = 800; // 8 squares per second
        respawnSpeed = 1400; // 14 squares per second
    }

    public void update(Vec2 start, Vec2 end, Type type) {
        p0 = start;
        p3 = end;
        this.type = type;

        // fill in missing start or end points and calculate start and end points on the arc
        View view; // the view the player is currently in
        switch (type) {
            case START:
                view = AppLogic.game.getView(p3.x, p3.y);
                if (view != null) {
                    // when the end is inside a view, make the start just outside that view to the left
                    float leftEdge = view.getTopLeft().x;
                    float diff = p3.x - leftEdge;
                    p0 = new Vec2(leftEdge - diff, p3.y);
                    position = 0.3f;
                } else {
                    // if not in a view, just do a demo start transition
                    p0 = new Vec2(p3.x - 300, p3.y - 100);
                    position = 0;
                }
                positionEnd = 1;
                break;
            case END:
                view = AppLogic.game.getView(p0.x, p0.y);
                if (view != null) {
                    // when the start is inside a view, make the end just outside that view to the right
                    float rightEdge = view.getBottomRight().x;
                    float diff = rightEdge - p0.x;
                    p3 = new Vec2(rightEdge + diff, p0.y);
                    positionEnd = 0.7f;
                } else {
                    // if not in a view, just do a demo end transition
                    p3 = new Vec2(p0.x + 300, p0.y + 100);
                    positionEnd = 1;
                }
                position = 0;
                break;
        }

        // figure out movement speed
        float distanceToTravel = (float) Math.sqrt(Math.pow((p0.x - p3.x), 2) + Math.pow((p0.y - p3.y), 2));
        float distancePerSecond; // how far the transition travels in a second
        if (type == Type.DEATH) {
            distancePerSecond = respawnSpeed;
        } else {
            distancePerSecond = normalSpeed;
        }
        movementDuration = distanceToTravel / distancePerSecond;

        // figure out p1 and p2 // TODO: this could be made a lot better
        float xDistance = Math.abs(p0.x - p3.x);
        float yOffset = xDistance / 5;
        p1 = new Vec2(p0.x, p0.y - yOffset);
        p2 = new Vec2(p3.x, p3.y - yOffset);

        // start the transition
        isActive = true;
        PApplet.print("Player Transition started");
    }

    public void step(float deltaTime) {
        // prevent running when inactive or missing data
        if (!isActive || p0 == null || p3 == null || p1 == null || p2 == null) {
            return;
        }

        if (position >= positionEnd) {
            position = 0; // reset
            isActive = false; // stop
            if (AppLogic.game.player != null) {
                switch (type) {
                    case START:
                    case TRANSITION:
                    case DEATH:
                        AppLogic.game.player.setActive(true);
                        break;
                }
            }
        } else {
            position += deltaTime / movementDuration;
        }

        point = calculateBezierPoint(p0, p1, p2, p3, position);


    }

    public Vec2 calculateBezierPoint(Vec2 p0, Vec2 p1, Vec2 p2, Vec2 p3, float t) {
        float r = 1f - t;
        float f0 = r * r * r;
        float f1 = r * r * t * 3;
        float f2 = r * t * t * 3;
        float f3 = t * t * t;
        return new Vec2(
                f0 * p0.x + f1 * p1.x + f2 * p2.x + f3 * p3.x,
                f0 * p0.y + f1 * p1.y + f2 * p2.y + f3 * p3.y
        );
    }

    public Vec2 getCenter() {
        return point;
    }

    public void drawVisualisation(PGraphics g, float scale) {
        // draw the curve and connecting lines
        g.noFill();
        g.stroke(80);
        g.strokeWeight(0.5f * scale); // 120 / scale
        g.bezier(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        g.stroke(150);
        g.strokeWeight(0.25f * scale); // 80 / scale
        g.line(p0.x, p0.y, p1.x, p1.y);
        g.line(p1.x, p1.y, p2.x, p2.y);
        g.line(p2.x, p2.y, p3.x, p3.y);

        g.rectMode(CENTER);
        g.noStroke();
        float rectSize = 2 * scale; // 800 / scale
        g.fill(255, 0, 0); // red
        g.rect(p0.x, p0.y, rectSize, rectSize);
        g.fill(0, 255, 0); // green
        g.rect(p1.x, p1.y, rectSize, rectSize);

        g.fill(0, 255, 0); // green
        g.rect(p2.x, p2.y, rectSize, rectSize);
        g.fill(255, 0, 0); // red
        g.rect(p3.x, p3.y, rectSize, rectSize);

        g.fill(0, 0, 255); // blue
        g.rect(point.x, point.y, rectSize, rectSize);
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Should the current transition be used to control the auto camera instead of the player.
     *
     * @return true if this transition should be tracked instead of the player
     */
    public boolean isCameraDominant() {
        // is the transition active and of type TRANSITION or DEATH
        return isActive && (type == Type.TRANSITION || type == Type.DEATH);
    }

    public float getSize() {
        return size;
    }

    public Vec2 getStart() {
        return p0;
    }

    public Vec2 getEnd() {
        return p3;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        TRANSITION, // moving between players within a level
        START, // beginning new level
        END, // ending a level
        DEATH // respawn
    }
}
