package net.lsrp.kingdom.graphics;

import java.util.Random;

import net.lsrp.kingdom.entity.mob.Mob;
import net.lsrp.kingdom.entity.projectile.Projectile;
import net.lsrp.kingdom.level.tile.Tile;

public class Screen {

	public int width, height;
	public int[] pixels;
	public final int MAP_SIZE = 64;
	public final int MAP_SIZE_MASK = MAP_SIZE - 1;
	
	public static int xOffset, yOffset;
	
	public int[] tiles = new int[MAP_SIZE * MAP_SIZE];
	
	private Random random = new Random();
	
	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height]; // 50400
		
		for(int i = 0; i < MAP_SIZE * MAP_SIZE; i++) {
			tiles[i] = random.nextInt(0xFFFFFF);
			tiles[0] = 0x000000;
		}
	}
	
	public void clear() {
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
	}
	
	public void renderSprite(int xp, int yp, Sprite sprite) {
		for(int y = 0; y < sprite.SIZE; y++) {
			int ya = y + yp;
			for(int x = 0; x < sprite.SIZE; x++) {
				int xa = x + xp;
				
				if(xa < -sprite.SIZE || xa >= width || ya < 0 || ya >= height) break;
				if(xa < 0) xa = 0; 
				
				int col = sprite.pixels[x + y * sprite.SIZE];
				if(col != 0xFFFF00FF)
					pixels[xa + ya * width] = col;
			}
		}
	}
	
	public void renderProjectile(int xp, int yp, Projectile p) {
		xp -= xOffset;
		yp -= yOffset;
		
		for(int y = 0; y < p.getSpriteSize(); y++) {
			int ya = y + yp;
			for(int x = 0; x < p.getSpriteSize(); x++) {
				int xa = x + xp;
				if(xa < -p.getSpriteSize() || xa >= width || ya < 0 || ya >= height) break;
				if(xa < 0) xa = 0; 
				
				int col = p.getSprite().pixels[x + y * p.getSpriteSize()];
				if(col != 0xFFFF00FF)
					pixels[xa + ya * width] = col;
			}
		}
	}
	
	public void renderTile(int xp, int yp, Tile tile) {
		xp -= xOffset;
		yp -= yOffset;
		
		for(int y = 0; y < tile.sprite.SIZE; y++) {
			int ya = y + yp;
			for(int x = 0; x < tile.sprite.SIZE; x++) {
				int xa = x + xp;
				if(xa < -tile.sprite.SIZE || xa >= width || ya < 0 || ya >= height) break;
				if(xa < 0) xa = 0; 
				
				int col = tile.sprite.pixels[x + y * tile.sprite.SIZE];
				if(col != 0xFFFF00FF)
					pixels[xa + ya * width] = col;
			}
		}
	}
	
	public void renderPlayer(int xp, int yp, Sprite sprite, int flip) {
		xp -= xOffset;
		yp -= yOffset;
		
		for(int y = 0; y < 32; y++) {
			int ya = y + yp;
			int ys = y;
			if(flip == 2 || flip == 3) ys = 31 -y;
			
			for(int x = 0; x < 32; x++) {
				int xa = x + xp;
				int xs = x;
				if(flip == 1 || flip == 3)
					xs = 31 - x;
				
				if(xa < -32 || xa >= width || ya < 0 || ya >= height) break;
				if(xa < 0) xa = 0; 
				
				int col = sprite.pixels[xs + ys * 32];
				if(col != 0xFFFF00FF)
					pixels[xa + ya * width] = col;
			}
		}		
	}
	
	public void renderPlayerTag(Mob mob) {
		int xp = mob.x - xOffset - 10;
		int yp = mob.y - yOffset - 20;
		
		//System.out.println("x: " + xp + " | y: " + yp + " | u: " + mob.name);
		
		for(int y = 0; y < 2; y++) {
			int ya = y + yp;
			for(int x = 0; x < 20; x++) {
				int xa = x + xp;
				if(xa < -32 || xa >= width || ya < 0 || ya >= height) break;
				if(xa < 0) xa = 0; 
				
				pixels[xa + ya * width] = 0x00FF0055;
				
				if(mob.getHealth() / 5.0 > x)
					pixels[xa + ya * width] = 0xFF00FF00;
			}
		}		
	}
	
	public void renderParticle(int xp, int yp, int color) {
		int ya = yp - yOffset;
		int xa = xp - xOffset;
		
		if(xa > 0 && xa < width && ya > 0 && ya < height) {
			if(color != 0xFFFF00FF)
				pixels[xa + ya * width] = color;
		}
	}
	
	public void setOffset(int xOffset, int yOffset) {
		Screen.xOffset = xOffset;
		Screen.yOffset = yOffset;
	}
}
