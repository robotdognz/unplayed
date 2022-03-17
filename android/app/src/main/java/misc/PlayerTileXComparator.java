package misc;

import java.util.Comparator;

import objects.Tile;

public class PlayerTileXComparator implements Comparator<Tile> {

	// this is used when checking if the player is inside a tunnel
	// tiles normally sort themselves by X, then Y, this sorts by Y, then X. It's
	// called XComparator because it is used when checking for tiles left and right
	// of the player.

	@Override
	public int compare(Tile a, Tile b) {
		float Y = a.getY();
		float otherY = b.getY();
		if (Y > otherY) {
			return 1;
		} else if (Y < otherY) {
			return -1;
		} else {
			float X = a.getX();
			float otherX = b.getX();
			if (X > otherX) {
				return 1;
			} else if (X < otherX) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
