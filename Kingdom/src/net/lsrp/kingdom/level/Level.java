package net.lsrp.kingdom.level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.entity.projectile.Projectile;
import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.level.tile.Tile;

public class Level {
	
	protected int width, height;
	protected int[] tilesInt;
	protected int[] tiles;
	
	public static Level level;
	
	private static List<Entity> entities = new ArrayList<Entity>();
	private static List<Projectile> projectiles = new ArrayList<Projectile>();
	
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
	
	public void update(double delta) {
		for(int i = 0; i < entities.size(); i++) {
			entities.get(i).update(delta);
		}
		
		for(int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).update(delta);
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
	
	public static void add(Entity e) {
		entities.add(e);
	}
	
	public static void remove(Entity e) {
		Iterator<Entity> i = entities.iterator();
		while(i.hasNext()) {
			if(i.next().equals(e))
				i.remove();
		}
	}
	
	public void addProjectile(Projectile p) {
		projectiles.add(p);
	}
	
	public List<Projectile> getProjectiles() {
		return projectiles;
	}
	
	public Tile getTile(int x, int y) {
		if(x < 0 || y < 0 || x >= width || y >= height) return Tile.voidTile;
		if(tiles[x + y * width] == Tile.col_grass) return Tile.grass;
		if(tiles[x + y * width] == Tile.col_brick_wall) return Tile.brick_wall;
		if(tiles[x + y * width] == Tile.col_mossy_brick) return Tile.mossy_brick;
		if(tiles[x + y * width] == Tile.col_water) return Tile.water;
		if(tiles[x + y * width] == Tile.col_spawn) return Tile.spawner;
		
		return Tile.voidTile;
	}
	
	public TileCoordinate getSpawn() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(getTile(x, y) == Tile.spawner) {
					return new TileCoordinate(x, y);
				}
			}
		}
		return new TileCoordinate(0, 0);
	}
	
	public int[] getTiles() {
		return tiles;
	}
}
