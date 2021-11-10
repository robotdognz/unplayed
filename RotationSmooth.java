package game.player;

public class RotationSmooth {
	private float oldAngle;
	private float newAngle;

	private boolean isFinished = false;

	private int steps;
	private int step;
	private float stepSize;

	public RotationSmooth(float oldAngle, float newAngle) {
		this.oldAngle = oldAngle;
		this.newAngle = newAngle;

		float difference = Math.abs(oldAngle - newAngle);

//		this.step = 0;
//		this.steps = (int) (difference / 5.0);
//
		if (newAngle < oldAngle) {
			difference = -difference;
		}
//
//		this.stepSize = difference * (1 / (float) steps);

		float animationLength = 0.5f; // half a second

		this.stepSize = difference / animationLength;
	}

	public void deltaStep(float deltaTime) {
		oldAngle += stepSize;

		if (oldAngle >= newAngle) {
			isFinished = true;
		}

//		step += 1;
//		if (step == steps) {
//			isFinished = true;
//		}
	}

	public float getAngle() {
		return oldAngle;
	}

	public boolean isFinished() {
		return isFinished;
	}
}
