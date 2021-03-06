package net.lsrp.kingdom.graphics;

public class Sprite {

	public final int SIZE;
	private int x, y;
	public int[] pixels;
	private SpriteSheet sheet;
	
	public static Sprite grass = new Sprite(16, 0, 0, SpriteSheet.tiles);
	public static Sprite mossy_brick = new Sprite(16, 0, 4, SpriteSheet.tiles);
	public static Sprite water = new Sprite(16, 0, 2, SpriteSheet.tiles);
	public static Sprite brick_wall = new Sprite(16, 0, 3, SpriteSheet.tiles);
	public static Sprite voidSprite = new Sprite(16, 0xFF1B87E0);
	
	//Player Sprites
	public static Sprite playerf = new Sprite(32, 0, 5, SpriteSheet.tiles);
	public static Sprite playerb = new Sprite(32, 2, 5, SpriteSheet.tiles);
	public static Sprite players = new Sprite(32, 1, 5, SpriteSheet.tiles);
	
	public static Sprite playerf1 = new Sprite(32, 0, 6, SpriteSheet.tiles);
	public static Sprite playerf2 = new Sprite(32, 0, 7, SpriteSheet.tiles);
	
	public static Sprite playerb1 = new Sprite(32, 2, 6, SpriteSheet.tiles);
	public static Sprite playerb2 = new Sprite(32, 2, 7, SpriteSheet.tiles);
	
	public static Sprite players1 = new Sprite(32, 1, 6, SpriteSheet.tiles);
	public static Sprite players2 = new Sprite(32, 1, 7, SpriteSheet.tiles);
	
	
	//Enemy Sprites
	public static Sprite enemyf = new Sprite(32, 3, 5, SpriteSheet.tiles);
	public static Sprite enemyb = new Sprite(32, 5, 5, SpriteSheet.tiles);
	public static Sprite enemys = new Sprite(32, 4, 5, SpriteSheet.tiles);
	
	public static Sprite enemyf1 = new Sprite(32, 3, 6, SpriteSheet.tiles);
	public static Sprite enemyf2 = new Sprite(32, 3, 7, SpriteSheet.tiles);
	
	public static Sprite enemyb1 = new Sprite(32, 5, 6, SpriteSheet.tiles);
	public static Sprite enemyb2 = new Sprite(32, 5, 7, SpriteSheet.tiles);
	
	public static Sprite enemys1 = new Sprite(32, 4, 6, SpriteSheet.tiles);
	public static Sprite enemys2 = new Sprite(32, 4, 7, SpriteSheet.tiles);
	
	//Projectiles
	public static Sprite slow_projectile = new Sprite(16, 0, 0, SpriteSheet.projectile_magic);
	

	public Sprite(int size, int x, int y, SpriteSheet sheet) {
		this.SIZE = size;
		pixels = new int[SIZE * SIZE];
		this.x = x * size;
		this.y = y * size;
		this.sheet = sheet;
		load();
	}
	
	public Sprite(int size, int color) {
		this.SIZE = size;
		pixels = new int[SIZE * SIZE];
		setColor(color);
	}
	
	private void setColor(int color) {
		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE; x++) {
				pixels[x + y * SIZE] = color;
			}
		}
	}
	
	private void load() {
		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE; x++) {
				pixels[x + y * SIZE] = sheet.pixels[(x + this.x) + (y + this.y) * sheet.SIZE];
			}
		}
	}
}
