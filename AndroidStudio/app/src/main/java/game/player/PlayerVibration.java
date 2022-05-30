package game.player;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import misc.CountdownTimer;
import misc.Vibe;

public class PlayerVibration {

	// old filtering system (this one is kept because it is used by rotation smooth, yuck)
	public List<PhysicsImpact> impacts;
	private float timeout;
	private long timeoutLong;
	private long currentTime;
	private CountdownTimer vibrationMonitor;
	private int previousImpulseOld;
	private static int maximum;
	private static int minimum;

	// new filtering system (this one works)
	static private float currentImpulse;
	static private float previousImpulse;
	static private int vibeOutput;
	private static CountdownTimer vibrationCooldown;


	public PlayerVibration() {
		// old filtering system (this one is kept because it is used by rotation smooth, yuck)
		currentTime = System.nanoTime();
		impacts = new ArrayList<>();
		timeout = 0.25f; // how long the impacts are kept for in seconds
		timeoutLong = (long) (timeout * 1000000000); // translated to nanoseconds
		vibrationMonitor = new CountdownTimer(0.064f);
		vibrationCooldown = new CountdownTimer(0.020f);
		previousImpulseOld = -1;
		maximum = 22;
		minimum = 1;
	}

	public void step(float deltaTime) {
		// old filtering system (this one is kept because it is used by rotation smooth, yuck)
		vibrationMonitor.deltaStep(deltaTime);
		vibrationCooldown.deltaStep(deltaTime);
		currentTime = System.nanoTime();
		// remove old impacts
		for (int i = 0; i < impacts.size(); i++) {
			PhysicsImpact impact = impacts.get(i);
			if (impact.time + timeoutLong < currentTime) {
				impacts.remove(impact);
			} else {
				break;
			}
		}

		// new filtering system (this one works)
		if (vibeOutput > 0 && !vibrationCooldown.isRunning()) {
			Vibe.vibrate(vibeOutput);
			vibrationCooldown.start();
		}
		vibeOutput = 0;


	}

	public float getImpactHistory() {
		// old filtering system (this one is kept because it is used by rotation smooth, yuck)
		float total = 0;
		float max = 0;
		for (PhysicsImpact impact : impacts) {
			total += impact.impact;
			float current = Math.abs(impact.impact);
			if (current > max) {
				max = current;
			}
		}
		return total;
	}

	public static void EndStep() {
		// new filtering system (this one works)

		// get impulse and clear field
		float currentStep = currentImpulse;

		if (currentStep > 1 && currentImpulse != previousImpulse) {

			// store vibration so it can be used in Step()
			int tempVibe = (int) Math.round(currentStep * 0.4f);

			if (tempVibe > vibeOutput) {
				vibeOutput = tempVibe;
			}
		}

		// reset
		previousImpulse = currentImpulse;
		currentImpulse = 0;
	}

	public static void DeathBuzz() {
		Vibe.vibrate(20);
	}

	public void physicsImpact(float[] normalImpulses, float[] tangentImpulses) {

		// new filtering system (this one works)
		float impulse1 = normalImpulses[0] * Game.DeltaStep();
		float impulse2 = normalImpulses[1] * Game.DeltaStep();
		float newImpulse = Math.max(impulse1, impulse2);
		if (newImpulse > 3) { // uses 3 to stop vibration when moving in a slot
			// replace the current step's impulse with this impulse
			if (currentImpulse < newImpulse) {
				currentImpulse = newImpulse;
			}
		}


		// old filtering system (this one is kept because it is used by rotation smooth, yuck)

		// find total impulse power
		float total = 0;
		for (float impulse : normalImpulses) {
			total += impulse;
		}
		// store impact
		impacts.add(new PhysicsImpact(total, currentTime));
		if (total > 800) {
			int strength = Math.round(total / 200) * 200;
			strength = (int) Math.min(Math.max(Math.abs(strength / 1000), minimum), maximum); // 800
			if (!vibrationMonitor.isRunning()) {
				vibrationMonitor.start();
			} else {
				if (strength == previousImpulseOld) {
					vibrationMonitor.start();
				}
			}
			previousImpulseOld = strength;
		}
	}
}
