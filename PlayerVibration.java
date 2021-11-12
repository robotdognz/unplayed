package game.player;

import java.util.ArrayList;
import java.util.List;

import misc.Vibe;

public class PlayerVibration {

	private boolean vibeFrame; // has a vibration happened yet this frame

	public List<PhysicsImpact> impacts;

	private long timeout;

	private long currentTime;

	public PlayerVibration() {
		currentTime = System.nanoTime();
		impacts = new ArrayList<PhysicsImpact>();

		timeout = 1 * 1000000000;
	}

	public void step(float deltaTime) {
		vibeFrame = false; // clear vibeFrame

		currentTime = System.nanoTime();

		// remove old impacts
		for (int i = impacts.size() - 1; i >= 0; i--) {
			PhysicsImpact impact = impacts.get(i);
			if (currentTime - timeout > impact.time) {
				impacts.remove(impact);
			} else {
				break;
			}
		}
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
			
			impacts.add(new PhysicsImpact(total, System.nanoTime())); // currentTime

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
