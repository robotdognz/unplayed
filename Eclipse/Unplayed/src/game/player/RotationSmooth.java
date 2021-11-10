package game.player;

public class RotationSmooth {
	private float oldAngle;
	private float newAngle;

	private boolean isFinished = false;
//	private float lerpAmount;

	private int steps;
	private int step;
	private float stepSize;

	// TODO: this system needs to use delta

	public RotationSmooth(float oldAngle, float newAngle) {
		this.oldAngle = oldAngle;
		this.newAngle = newAngle;

//		this.lerpAmount = 0.4f;

		float difference = Math.abs(oldAngle - newAngle);
		if (newAngle < oldAngle) {
			difference = -difference;
		}

		this.step = 0;
		this.steps = 3;

		this.stepSize = difference * (1 / (float) steps);
	}

	public float getAngle() {
//		oldAngle = PApplet.lerp(oldAngle, newAngle, lerpAmount);
//		if (Math.abs(oldAngle-newAngle) < 0.001) {
//			this.isFinished = true;
//		}

		oldAngle += stepSize;
		step += 1;
		if (step == steps) {
			isFinished = true;
		}

		return oldAngle;
	}

	public boolean isFinished() {
		return isFinished;
	}
}
