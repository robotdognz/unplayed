package game.player;

import static processing.core.PConstants.*;

import org.jbox2d.common.Vec2;

import processing.core.PApplet;
import processing.core.PGraphics;

public class PlayerTransition {
    final float movementSpeed;

    Vec2 p0;
    Vec2 p1;
    Vec2 p2;
    Vec2 p3;

    float distance;

    Vec2 point;

    float position = 0;

    private boolean isActive;

    public PlayerTransition(Vec2 start, Vec2 end) {
        update(start, end);
        point = start.clone();

        movementSpeed = 0.8f; // 1 = 1 second, 0.5 = 2 seconds, etc.
    }

    public void update(Vec2 start, Vec2 end) {
        p0 = start;
        p3 = end;

        // TODO: write robust algorithm for figuring out p1 and p2

        distance = (float) Math.sqrt(Math.pow((p0.x - p3.x), 2) + Math.pow((p0.y - p3.y), 2));

        float offset = 300;
        p1 = new Vec2(start.x, start.y - offset / 3);
        p2 = new Vec2(end.x, end.y - offset / 3);

        isActive = true;
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

    public void draw(PGraphics g) {
        g.rectMode(CENTER);
        g.noStroke();
        float rectSize = 20;
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
}
