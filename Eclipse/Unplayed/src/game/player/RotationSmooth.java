package game.player;

public class RotationSmooth {
	private float oldAngle;
	private float newAngle;

	private boolean isFinished = false;

	private float stepSize;

	public RotationSmooth(float oldAngle, float newAngle) {
		this.oldAngle = oldAngle;
		this.newAngle = newAngle;

		float difference = Math.abs(oldAngle - newAngle);

		float animationLength = difference / 350;// 0.1f; // portion of a second it takes the animation to finish

		if (newAngle < oldAngle) {
			difference = -difference;
		}

		this.stepSize = difference / animationLength;
	}

	public void deltaStep(float deltaTime) {

		// if the diff between the stepped angle and new angle is less than the diff
		// between the old angle and new angle, step
		// else do nothing and set is finished

		float steppedAngle = oldAngle + stepSize * deltaTime;

		if (Math.abs(steppedAngle - newAngle) < Math.abs(oldAngle - newAngle)) {
			oldAngle = steppedAngle;
		} else {
			isFinished = true;
		}

//		oldAngle += stepSize * deltaTime;

//		if (Math.abs(oldAngle - newAngle) <= 3) { // 3 is an arbitrary number
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
