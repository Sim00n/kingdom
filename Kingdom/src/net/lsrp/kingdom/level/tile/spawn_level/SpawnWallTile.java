package net.lsrp.kingdom.level.tile.spawn_level;

import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.level.tile.Tile;

public class SpawnWallTile extends Tile {

	public SpawnWallTile(Sprite sprite) {
		super(sprite);
	}
	
	@Override
	public void render(int x, int y, Screen screen) {
		screen.renderTile(x << 4, y << 4, this);
	}
	
	@Override
	public boolean solid() {
		return true;
	}
}
