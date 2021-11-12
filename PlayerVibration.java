package game.player;

import misc.Vibe;

public class PlayerVibration {
	
	private boolean vibeFrame; // has a vibration happened yet this frame

	public PlayerVibration() {
	}
	
	public void step(float deltaTime) {
		vibeFrame = false; // clear vibeFrame
	}

	public void physicsImpact(float[] impulses) {
		// find total impulse power
		float total = 0;
		for (float impulse : impulses) {
			total += impulse;
		}

		// TODO: this doesn't work because if you jump in one spot at the same height,
		// it stops the vibration
		// could be improved by adding a short timer to it

//				// check if we already did one like this
//				float impulseDifference = Math.abs(total - previousImpulse);
//				if (previousImpulse != 0 && impulseDifference < 4) {
//					PApplet.println(total + " skipped by previousImpulse");
//					return;
//				} else {
////					previousImpulse = total;
//				}

		if (total > 800 && !vibeFrame) { // 400

			// Math.abs returns positive no matter what goes in
			// Math.log returns the log of the number it is given
			int strength = (int) Math.max(Math.abs(total / 1000), 1); // 800
			Vibe.vibrate(strength);
//					PApplet.println(total + " " + strength);
			vibeFrame = true;
//					previousImpulse = total;
			return;
		}
	}
}
