package misc;

public enum CollisionEnum {
	PLAYER_BODY, PLAYER_SENSOR,

	TILE, // used by player environment sensor to detect tile sensor
	GROUND, LEFT_WALL, RIGHT_WALL,
	
	EVENT,
	
	ROOF_BARRIER
}
