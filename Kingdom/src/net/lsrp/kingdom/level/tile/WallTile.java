package net.lsrp.kingdom.level.tile;

import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;

public class WallTile extends Tile {

	public WallTile(Sprite sprite) {
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
