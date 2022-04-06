package game.player;

import static processing.core.PConstants.*;

import org.jbox2d.common.Vec2;

import processing.core.PApplet;
import processing.core.PGraphics;

public class PlayerTransition {
    final float movementSpeed;

    // bezier curve coordinates
    private Vec2 p0;
    private Vec2 p1;
    private Vec2 p2;
    private Vec2 p3;

    private float distance; // the current distance being traversed by the transition

    private Vec2 point; // where the transition effect is currently
    private float position = 0; // where the transition is on its journey between 0 and 1
    private float size;

    private Type type;

    private boolean isActive; // true if a transition is currently happening

    public PlayerTransition(Vec2 start, Vec2 end) {
        point = start.clone();

        movementSpeed = 0.8f; // 1 = 1 second, 0.5 = 2 seconds, etc.

        size = 50;
    }

    public void update(Vec2 start, Vec2 end, Type type) {
        p0 = start;
        p3 = end;
        this.type = type;

        if (type == Type.START) {
            // TODO: actually calculate where the start should be
            p0 = new Vec2(end.x - 500, end.y);
        }

        // TODO: write robust algorithm for figuring out p1 and p2

        distance = (float) Math.sqrt(Math.pow((p0.x - p3.x), 2) + Math.pow((p0.y - p3.y), 2));

        float offset = 300;
        p1 = new Vec2(p0.x, p0.y - offset / 3);
        p2 = new Vec2(end.x, end.y - offset / 3);

        isActive = true;
        PApplet.print("Player Transition started");
    }

    public void step(float deltaTime) {
        // prevent running when inactive or missing data
        if (!isActive || p0 == null || p1 == null || p2 == null || p3 == null) {
            return;
        }

        if (position >= 0.99) {
            position = 0; // reset
            isActive = false; // stop
        } else {
            position += deltaTime * movementSpeed;
//            position = PApplet.lerp(position, 1, 2 * deltaTime); // lower is slower
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

    public void draw(PGraphics g, float scale) {
        // draw the curve and connecting lines
        g.noFill();
        g.stroke(80);
        g.strokeWeight(120 / scale);
        g.bezier(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        g.stroke(150);
        g.strokeWeight(80 / scale);
        g.line(p0.x, p0.y, p1.x, p1.y);
        g.line(p1.x, p1.y, p2.x, p2.y);
        g.line(p2.x, p2.y, p3.x, p3.y);

        g.rectMode(CENTER);
        g.noStroke();
        float rectSize = 800 / scale;
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

    public boolean isCameraDominant() {
        if (isActive && type == Type.TRANSITION) {
            return true;
        }
        return false;
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

    public enum Type {
        TRANSITION, // moving between players within a level
        START, // beginning new level
        END, // ending a level
        DEATH // respawn
    }
}
