package objects;

import static processing.core.PConstants.BLEND;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.MULTIPLY;

import java.io.File;

import handlers.BackgroundHandler;
import handlers.TextureCache;
import processing.core.PApplet;
import processing.core.PVector;

public class Background extends PageViewObject {

    private final boolean hasTexture;
    private BackgroundHandler texture;

    private boolean hasShadow = false; // background image should have a shadow
    private int shadowOffset; // the absolute amount to offset the shadow by
    private int shadow; // the relative amount to offset the shadow by

    public Background(PApplet p, TextureCache textureCache, File file, PVector position) {
        super(p, position, 1, 1);

        if (file != null && textureCache != null && textureCache.getBackgroundMap().containsKey(file)) {
            this.texture = textureCache.getBackgroundMap().get(file);
            hasTexture = true;
            // TODO: textures are stored in grid amounts 1x1 etc, whereas actual world
            // objects are stored as 100x100 etc. This should be fixed so everything uses
            // the 1x1 system. Then remove the * 100 from the two below lines
            setWidth(this.texture.getWidth() * 100); // 100
            setHeight(this.texture.getHeight() * 100); // 100
            this.hasShadow = this.texture.hasShadow();

            this.shadowOffset = 9;
            this.shadow = 9;

        } else {
            hasTexture = false;
            setWidth(100);
            setHeight(100);
        }


        setPosition(position);
    }

    @Override
    public void draw(float scale) {

        if (hasTexture) {
            // texture isn't missing
            p.pushMatrix();
            p.translate(position.x, position.y);
            p.scale(size); // size the page will appear in the page view

            if (hasShadow) { // is a solid shape
                // draw the shadow
                p.translate(shadow, shadow);
                p.fill(0, 40);
                p.noStroke();
                p.rectMode(CENTER);
                p.rotate(PApplet.radians(angle)); // rotate the page
                p.rect(0, 0, getWidth(), getHeight());
                p.rotate(PApplet.radians(-angle)); // rotate the page
                p.translate(-shadow, -shadow);
            } else {
                p.blendMode(MULTIPLY); // render it multiplied
            }

            p.rotate(PApplet.radians(angle)); // rotate the page
            p.imageMode(CENTER);
            // flip
            if (flipX != 0 || flipY != 0) {
                p.scale(flipX, flipY); // flip the page
            }
            p.image(texture.getSprite(0), 0, 0, getWidth(), getHeight()); // draw the page
            p.popMatrix();

            p.blendMode(BLEND); // back to normal rendering

        } else {
            // texture is missing
            p.pushMatrix();
            p.translate(position.x, position.y);
            p.scale(size); // size the background will appear in the page view
            p.rotate(PApplet.radians(angle)); // rotate the page
            p.scale(flipX, flipY); // flip the page
            p.noStroke();
            p.fill(255, 0, 0, 150);
            p.rectMode(CENTER);
            p.rect(0, 0, getWidth(), getHeight());
            p.popMatrix();
        }

    }

    public void setSize(float size) {
        super.setSize(size);
        updateShadow();
    }

    public void addSize(float size) {
        super.addSize(size);
        updateShadow();
    }

    private void updateShadow() {
        this.shadow = (int) (shadowOffset / size);
    }

    public boolean fixedSize() {
        return texture.fixedSize();
    }

    @Override
    public String getName() {
        return "background";
    }

    public File getFile() {
        if (texture != null) {
            return texture.getFile();
        } else {
            return null;
        }
    }
}
