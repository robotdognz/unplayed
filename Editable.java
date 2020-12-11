package objects;

public class Editable extends Rectangle {
	protected float flipX;
	protected float flipY;
	protected float size;
	protected float angle;

	public Editable(float x, float y, float rWidth, float rHeight) {
		super(x, y, rWidth, rHeight);
		flipX = 1;
		flipY = 1;
		size = 1;
		angle = 0;
	}
	
//	public void addPosition(float x, float y) {
//		setX(getX()+x);
//		setY(getY()+y);
//	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public void addAngle(float angle) {
		this.angle += angle;
	}

	public float getAngle() {
		return angle;
	}

	public void flipH() {
		if (flipX == 1) {
			flipX = -1;
		} else {
			flipX = 1;
		}
	}

	public boolean isFlippedH() {
		if (flipX == 1) {
			return false;
		} else {
			return true;
		}
	}

	public void flipV() {
		if (flipY == 1) {
			flipY = -1;
		} else {
			flipY = 1;
		}
	}

	public boolean isFlippedV() {
		if (flipY == 1) {
			return false;
		} else {
			return true;
		}
	}

}
