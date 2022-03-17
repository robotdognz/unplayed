package game.player;

//import editor.DebugOutput;

public class RotationSmooth {
	private float currentAngle;
	private float finalAngle;

	private boolean isFinished = false;

	private float stepSize;

	// TODO: another way could be to check the max velocity of the last part of a second and use that
	// not the way I did before, completely unrelated to physics impacts

	public RotationSmooth(float oldAngle, float newAngle, float impact) {
		this.currentAngle = oldAngle;
		this.finalAngle = newAngle;

		float difference = Math.abs(oldAngle - newAngle);

		// portion of a second it takes the animation to finish
		float animationLength = difference / (300 + (impact / 400)); // calculated relative to rotation amount
																		// (difference)

		// turn absolute difference back into standard difference
		if (newAngle < oldAngle) {
			difference = -difference;
		}

		// calculate the size of the step to be taken each frame
		this.stepSize = difference / animationLength;

//		String output = "Old Angle: " + PApplet.nf(oldAngle, 0, 1) + ", New Angle: " + PApplet.nf(newAngle, 0, 1) + "\n"
//				+ "Impact: " + PApplet.nf(impact / 400, 0, 3) + ", Animation: " + PApplet.nf(animationLength, 0, 3);
//
//		DebugOutput.pushMessage(output, 4);
	}

	public void deltaStep(float deltaTime) {

		float steppedAngle = currentAngle + stepSize * deltaTime;

		// check if the stepped angle is closer to the final angle than the current
		// angle
		if (Math.abs(steppedAngle - finalAngle) < Math.abs(currentAngle - finalAngle)) {
			// apply the current step
			currentAngle = steppedAngle;
		} else {
			// the rotation is complete
			currentAngle = finalAngle;
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
