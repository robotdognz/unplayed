package game.player;

import org.jbox2d.common.Vec2;

import editor.DebugMessage;
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

	public RotationSmooth(float oldAngle, float newAngle, Vec2 velocity, float angularVel) {
		this.currentAngle = oldAngle;
		this.finalAngle = newAngle;

		float difference = Math.abs(oldAngle - newAngle);

		float absVelocity = Math.abs(velocity.y);
		if (absVelocity > 150) {
			absVelocity = 250;
		}

		// portion of a second it takes the animation to finish
		float animationLength = difference / (400 + absVelocity); // calculated relative to rotation amount (difference)
//		float animationLength = difference / 400; // calculated relative to rotation amount

		// turn absolute difference back into standard difference
		if (newAngle < oldAngle) {
			difference = -difference;
		}

		// calculate the size of the step to be taken each frame
		this.stepSize = difference / animationLength;

		String output = "Old Angle: " + oldAngle + ", New Angle: " + newAngle;

		DebugMessage.pushMessage(output);

		output = "Velocity: " + absVelocity + ", Animation Length: " + animationLength;

		DebugMessage.pushMessage(output);

//		PApplet.print("Old Angle: " + oldAngle + ", New Angle: " + newAngle + ", Velocity: " + absVelocity
//				+ ", Animation Length: " + animationLength + "\n");
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
