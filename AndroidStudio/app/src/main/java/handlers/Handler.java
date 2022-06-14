package handlers;

import static processing.core.PConstants.CENTER;

import java.io.File;

import editor.DebugOutput;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public abstract class Handler {
    protected PApplet p;
    protected TextureCache texture;
    protected File datapath;

    private int gridResolution; // maximum resolution of single grid square
    private final boolean useLODs; // should LODs be used, or just max resolution (LODFull)?
    private PImage LODFull; // 256, if max is 256
    private PImage LODHalf = null; // 128, if max is 256
    private PImage LODQuarter = null; // 64, if max is 256
    private PImage LODEighth = null; // 32, if max is 256
    private PImage LODSixteenth = null; // 16, if max is 256

    protected int width;
    protected int height;

    protected float widthRenderRatio;
    protected float heightRenderRatio;

    protected boolean isRotatable; // can the editor default angle for this object be changed
    protected int editorRotation; // angle to add when creating new instance with level editor

    public Handler(PApplet p, TextureCache texture, File file, int width, int height, boolean useLODs) {
        this.p = p;
        this.texture = texture;
        this.datapath = file;

        this.width = width;
        this.height = height;

        this.useLODs = useLODs;

        try {
            String path = file.toString();
            LODFull = p.loadImage(path);
//            this.gridResolution = findNextPowerOf2(LODFull.width / width); // smallest this should be is 16
//            PApplet.print(path + " - Actual Resolution: " + LODFull.width + "x" + LODFull.height);
//            LODFull.resize(this.gridResolution * width, this.gridResolution * height);
//            PApplet.print(path + " - Fixed Resolution: " + LODFull.width + "x" + LODFull.height);

        } catch (Exception e) {
            // set sprite to file not found image
        }

        // setup rendering ratios for use in the editor bottom scroll bar
        if (width > height) {
            heightRenderRatio = (float) height / width;
            widthRenderRatio = 1;
        } else if (width < height) {
            heightRenderRatio = 1;
            widthRenderRatio = (float) width / height;
        } else {
            widthRenderRatio = 1;
            heightRenderRatio = 1;
        }

        this.isRotatable = true;
        this.editorRotation = 0;
    }

    // Compute power of two closest to `n`
    private static int findNextPowerOf2(int n) {

        // the +-7 comes from the how far off the worst one of Reuben's images is "sprt_030_1x1.png"
        int highestOneBit = Integer.highestOneBit(n + 7);
        int lowestOneBit = Integer.highestOneBit(n - 7);

//        PApplet.print("" + n + " - hi: " + highestOneBit + ", low:" + lowestOneBit);

        if (n == highestOneBit || n == lowestOneBit) {
            return n;
        } else if (Math.abs(highestOneBit - n) > Math.abs(lowestOneBit - n)) {
            // larger difference between n and highestOneBit than n and lowestOneBit
            return lowestOneBit;
        } else {
            // larger difference between n and lowestOneBit than n and highestOneBit
            return highestOneBit;
        }
    }

    public void setEditorAngle(float angle) {
        if (isRotatable) {
            editorRotation = (int) angle;
            DebugOutput.pushMessage("" + angle, 1);
        }
    }

    public int getEditorAngle() {
        return editorRotation;
    }

    public PImage getSprite(float scale) {
        if (useLODs) {
            if (scale > TextureCache.LOD32) {
                if (LODSixteenth == null) {
                    LODSixteenth = LODFull.get();
                    int currentResolution = gridResolution / 16; // 16, if max is 256
                    LODSixteenth.resize(currentResolution * width, currentResolution * height);
                }
                return LODSixteenth;
            } else if (scale > TextureCache.LOD64) {
                if (LODEighth == null) {
                    LODEighth = LODFull.get();
                    int currentResolution = gridResolution / 8; // 32, if max is 256
                    LODEighth.resize(currentResolution * width, currentResolution * height);
                }
                return LODEighth;
            } else if (scale > TextureCache.LOD128) {
                if (LODQuarter == null) {
                    LODQuarter = LODFull.get();
                    int currentResolution = gridResolution / 4; // 64, if max is 256
                    LODQuarter.resize(currentResolution * width, currentResolution * height);
                }
                return LODQuarter;
            } else if (scale > TextureCache.LOD256) {
                if (LODHalf == null) {
                    LODHalf = LODFull.get();
                    int currentResolution = gridResolution / 2; // 128, if max is 256
                    LODHalf.resize(currentResolution * width, currentResolution * height);
                }
                return LODHalf;
            } else {
                return LODFull;
            }
        }
        return LODFull;
    }

    public File getFile() {
        return datapath;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void drawEditor(float pX, float pY, float size) {
        // calculate how to scale the image so it appears in the editor bottom scroll
        // bar correctly and draw the scaled image
        if (editorRotation != 0) {
            p.pushMatrix();
            p.imageMode(CENTER);
            p.translate(pX, pY);
            p.rotate(PApplet.radians(editorRotation));
            p.image(getSprite(3), 0, 0, widthRenderRatio * size, heightRenderRatio * size); //getSprite(6)
            p.popMatrix();
        } else {
            p.image(getSprite(3), pX, pY, widthRenderRatio * size, heightRenderRatio * size); //getSprite(6)
        }
    }

    public void draw(PGraphics graphics, float x, float y, float scale) {
        graphics.image(getSprite(scale), x, y);
    }

    public void draw(PGraphics graphics, float x, float y, float width, float height, float scale) {
        graphics.image(getSprite(scale), x, y, width, height);
    }

    public void drawAll() {
        p.image(getSprite(TextureCache.LOD32), 0, 0, 100, 100);
        p.image(getSprite(TextureCache.LOD64), 0, 0, 100, 100);
        p.image(getSprite(TextureCache.LOD128), 0, 0, 100, 100);
        p.image(getSprite(TextureCache.LOD256), 0, 0, 100, 100);
    }
}
