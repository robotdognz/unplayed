package game.player;

public class PhysicsImpact {
	public float impact;
	public float velocity;
	public long time;
	
	public PhysicsImpact(float impact, float velocity, long time) {
		this.impact = impact;
		this.velocity = velocity;
		this.time = time;
	}
	
	public String toString() {
		return "" +  impact + " " + velocity + " : " + time;
	}
}
