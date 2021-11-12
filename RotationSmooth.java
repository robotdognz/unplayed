package game.player;

import org.jbox2d.common.Vec2;
import editor.DebugOutput;
import processing.core.PApplet;

public class RotationSmooth {
	private float currentAngle;
	private float finalAngle;

	private boolean isFinished = false;

	private float stepSize;

	// TODO: currently this system uses velocity from before the snap. It could
	// potentially be improved by storing the biggest physics impact from the half
	// second (or so, this would be one of the values to adjust) before the snap and
	// using that to determine how fast it should rotate

	public RotationSmooth(float oldAngle, float newAngle, float impact) {
		this.currentAngle = oldAngle;
		this.finalAngle = newAngle;

		float difference = Math.abs(oldAngle - newAngle);

//		float absVelocity = Math.abs(velocity.y);
//		if (absVelocity > 150) {
//			absVelocity = 250;
//		}

		// portion of a second it takes the animation to finish
		float animationLength = difference / (300 + (impact / 400)); // calculated relative to rotation amount
																		// (difference)
//		float animationLength = difference / 400; // calculated relative to rotation amount

		// turn absolute difference back into standard difference
		if (newAngle < oldAngle) {
			difference = -difference;
		}

		// calculate the size of the step to be taken each frame
		this.stepSize = difference / animationLength;

		String output = "Old Angle: " + PApplet.nf(oldAngle, 0, 1) + ", New Angle: " + PApplet.nf(newAngle, 0, 1) + "\n"
//				+ "Velocity: " + PApplet.nf(absVelocity, 0, 3) 
				+ ", Animation Length: " + PApplet.nf(animationLength, 0, 3);

		DebugOutput.pushMessage(output, 5);
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
