package game.player;

public class RotationSmooth {
	private float currentAngle;
	private float finalAngle;

	private boolean isFinished = false;

	private float stepSize;

	public RotationSmooth(float oldAngle, float newAngle) {
		this.currentAngle = oldAngle;
		this.finalAngle = newAngle;

		float difference = Math.abs(oldAngle - newAngle);

		// portion of a second it takes the animation to finish
		float animationLength = difference / 400; // calculated relative to rotation amount

		if (newAngle < oldAngle) {
			difference = -difference;
		}

		// calculate the size of the step to be taken each frame
		this.stepSize = difference / animationLength;
	}

	public void deltaStep(float deltaTime) {

		float steppedAngle = currentAngle + stepSize * deltaTime;

		// check if the stepped angle is closer to the final angle than the current angle
		if (Math.abs(steppedAngle - finalAngle) < Math.abs(currentAngle - finalAngle)) {
			// apply the current step
			currentAngle = steppedAngle;
		} else {
			// the rotation is complete
			isFinished = true;
		}

	}

	public float getAngle() {
		return currentAngle;
	}

	public boolean isFinished() {
		return isFinished;
	}
}
