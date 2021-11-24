package misc;

public enum CollisionEnum {
	PLAYER_BODY, PLAYER_SENSOR, PLAYER_EDGE,

	SOLID, // used by any fixtures the player should vibrate when hitting
	TILE, // used by player environment sensor to detect tile sensor
	GROUND, LEFT_WALL, RIGHT_WALL,
	
	EVENT,
	
	ROOF_BARRIER
}
