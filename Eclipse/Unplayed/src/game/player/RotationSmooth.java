package game.player;

public class RotationSmooth {
	private float oldAngle;

	private boolean isFinished = false;

	private int steps;
	private int step;
	private float stepSize;

	public RotationSmooth(float oldAngle, float newAngle) {
		this.oldAngle = oldAngle;

		float difference = Math.abs(oldAngle - newAngle);
		this.step = 0;
		this.steps = (int) (difference / 5);

		if (newAngle < oldAngle) {
			difference = -difference;
		}

		this.stepSize = difference * (1 / (float) steps);
	}

	public void step() {
		oldAngle += stepSize;
		step += 1;
		if (step == steps) {
			isFinished = true;
		}
	}

	public float getAngle() {
		return oldAngle;
	}

	public boolean isFinished() {
		return isFinished;
	}
}
