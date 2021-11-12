package game.player;

public class PhysicsImpact {
	public float impact;
	public long time;
	
	public PhysicsImpact(float impact, long time) {
		this.impact = impact;
		this.time = time;
	}
	
	public String toString() {
		return "" +  impact + " : " + time;
	}
}
