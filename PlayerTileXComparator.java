package misc;

import java.util.Comparator;

import objects.Tile;

public class PlayerTileXComparator implements Comparator<Tile> {

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