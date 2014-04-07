package net.lsrp.kingdom.level;

import java.util.ArrayList;
import java.util.List;

import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.entity.projectile.Projectile;
import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.level.tile.Tile;

public class Level {
	
	protected int width, height;
	protected int[] tilesInt;
	protected int[] tiles;
	
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Projectile> projectiles = new ArrayList<Projectile>();
	
	public static Level spawn = new SpawnLevel("/levels/spawn.png");
	
	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		tilesInt = new int[width * height];
		generateLevel();
	}
	
	public Level(String path) {
		loadLevel(path);
		generateLevel();
	}
	
	protected void generateLevel() {
	}
	
	protected void loadLevel(String path) {
	}
	
	public void update() {
		for(int i = 0; i < entities.size(); i++) {
			entities.get(i).update();
		}
		
		for(int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).update();
		}
	}
	
	public void render(int xScroll, int yScroll, Screen screen) {
		screen.setOffset(xScroll, yScroll);
		int x0 = xScroll >> 4;
		int x1 = (xScroll + screen.width + 16) >> 4;
		int y0 = yScroll >> 4;
		int y1 = (yScroll + screen.height + 16) >> 4;
		
		for(int y = y0; y < y1; y++) {
			for(int x = x0; x < x1; x++) {
				getTile(x, y).render(x, y, screen);
			}
		}
		
		for(int i = 0; i < entities.size(); i++) {
			entities.get(i).render(screen);
		}
		
		for(int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).render(screen);
		}
	}
	
	public void add(Entity e) {
		entities.add(e);
	}
	
	public void addProjectile(Projectile p) {
		projectiles.add(p);
	}
	
	public List<Projectile> getProjectiles() {
		return projectiles;
	}
	
	/*
	 * Grass = 0xFF00FF21
	 * Flower = 0xFFFFD800
	 * Rock = 0xFF545024
	 */
	public Tile getTile(int x, int y) {
		if(x < 0 || y < 0 || x >= width || y >= height) return Tile.voidTile;
		if(tiles[x + y * width] == Tile.col_spawn_floor) return Tile.spawnFloorTile;
		if(tiles[x + y * width] == Tile.col_spawn_grass) return Tile.spawnGrassTile;
		if(tiles[x + y * width] == Tile.col_spawn_hedge) return Tile.spawnHedgeTile;
		if(tiles[x + y * width] == Tile.col_spawn_wall1) return Tile.spawnWall1Tile;
		if(tiles[x + y * width] == Tile.col_spawn_wall2) return Tile.spawnWall2Tile;
		if(tiles[x + y * width] == Tile.col_spawn_water) return Tile.spawnWaterTile;
		
		
		return Tile.voidTile;
	}
	
	public int[] getTiles() {
		return tiles;
	}
}