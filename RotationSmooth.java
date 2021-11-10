package game.player;

public class RotationSmooth {
	private float oldAngle;
	private float newAngle;

	private boolean isFinished = false;
//	private float lerpAmount;

	private float stepSize;

	public RotationSmooth(float oldAngle, float newAngle) {
		this.oldAngle = oldAngle;
		this.newAngle = newAngle;

//		this.lerpAmount = 0.4f;

		float difference = Math.abs(oldAngle - newAngle);
		if (newAngle < oldAngle) {
			difference = -difference;
		}

		this.stepSize = difference * 0.25f;
	}

	public float getAngle() {
//		oldAngle = PApplet.lerp(oldAngle, newAngle, lerpAmount);
//		if (Math.abs(oldAngle-newAngle) < 0.001) {
//			this.isFinished = true;
//		}

		oldAngle += stepSize;
		if (oldAngle == newAngle) {
			isFinished = true;
		}

		return oldAngle;
	}

	public boolean isFinished() {
		return isFinished;
	}
}
