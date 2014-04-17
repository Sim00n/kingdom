package net.lsrp.kingdom.level.tile;

import net.lsrp.kingdom.entity.projectile.Projectile;
import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;

public class Tile {

	public int x, y;
	public Sprite sprite;
	
	public static Tile grass = new FloorTile(Sprite.grass);
	public static Tile mossy_brick = new FloorTile(Sprite.mossy_brick);
	public static Tile brick_wall = new WallTile(Sprite.brick_wall);
	public static Tile water = new WaterTile(Sprite.water);
	public static Tile voidTile = new VoidTile(Sprite.voidSprite);
	public static Tile spawner = new FloorTile(Sprite.grass);
		
	public static final int col_grass = 0xFF97FF63;
	public static final int col_mossy_brick = 0xFFFFB770;
	public static final int col_water = 0xFF28E9FF;
	public static final int col_brick_wall = 0xFF365B23;
	public static final int col_spawn = 0xFFFF071C;
	
	public Tile(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public void render(int x, int y, Screen screen) {
		screen.renderTile(x << 4, y << 4, this);
	}
	
	public boolean solid() {
		return false;
	}
	
	public boolean projectileCollision(Projectile p) {
		
		if(!p.isRemoved())
			if(p.y + p.sprite.SIZE > y && p.y < y + sprite.SIZE)
				if(p.x + p.sprite.SIZE > x && p.x < x + sprite.SIZE)
					return true;
		return false;
	}
}
