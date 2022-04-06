package game.player;

import static processing.core.PConstants.*;
import org.jbox2d.common.Vec2;
import processing.core.PApplet;
import processing.core.PGraphics;

public class BezierLerp {
    Vec2 p0;
    Vec2 p1;
    Vec2 p2;
    Vec2 p3;

    Vec2 point;

    float position = 0;

    private boolean isActive;

    public BezierLerp(Vec2 start, Vec2 end) {
        update(start, end);
        point = start.clone();

    }

    public void update(Vec2 start, Vec2 end){
        p0 = start;
        p3 = end;

        // TODO: write robust algorithm for figuring out p1 and p2

        float distance = (float) Math.sqrt(Math.pow((p0.x - p3.x) + Math.pow((p0.y - p3.y), 2), 2));
        p1 = new Vec2(start.x + distance / 3, start.y - distance / 3);
        p2 = new Vec2(end.x - distance / 3, end.y - distance / 3);

        isActive = true;
    }

    public void step(float deltaTime) {

        if (position >= 0.99) {
            position = 0;
        } else {
//            position += 0.02;
//            position = PApplet.lerp(position, 1, 0.05f);
            position = PApplet.lerp(position, 1, 4 * deltaTime);
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

    public Vec2 getCenter(){
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
