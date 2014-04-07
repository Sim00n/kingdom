package net.lsrp.kingdom.level.tile;

import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.level.tile.spawn_level.SpawnFloorTile;
import net.lsrp.kingdom.level.tile.spawn_level.SpawnGrassTile;
import net.lsrp.kingdom.level.tile.spawn_level.SpawnHedgeTile;
import net.lsrp.kingdom.level.tile.spawn_level.SpawnWallTile;
import net.lsrp.kingdom.level.tile.spawn_level.SpawnWaterTile;

public class Tile {

	public int x, y;
	public Sprite sprite;
	
	public static Tile grass = new GrassTile(Sprite.grass);
	public static Tile flower = new FlowerTile(Sprite.flower);
	public static Tile rock = new RockTile(Sprite.rock);
	public static Tile voidTile = new VoidTile(Sprite.voidSprite);
	
	public static Tile spawnGrassTile = new SpawnGrassTile(Sprite.spawn_grass);
	public static Tile spawnHedgeTile = new SpawnHedgeTile(Sprite.spawn_hedge);
	public static Tile spawnWaterTile = new SpawnWaterTile(Sprite.spawn_water);
	public static Tile spawnWall1Tile = new SpawnWallTile(Sprite.spawn_wall1);
	public static Tile spawnWall2Tile = new SpawnWallTile(Sprite.spawn_wall2);
	public static Tile spawnFloorTile = new SpawnFloorTile(Sprite.spawn_floor);
	
	public static final int col_spawn_grass = 0xFF97FF63;
	public static final int col_spawn_hedge = 0;
	public static final int col_spawn_water = 0;
	public static final int col_spawn_wall1 = 0xFF365B23;
	public static final int col_spawn_wall2 = 0xFF3F49FF;
	public static final int col_spawn_floor = 0xFFFFB770;
	
	
	public Tile(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public void render(int x, int y, Screen screen) {
	}
	
	public boolean solid() {
		return false;
	}
	
}