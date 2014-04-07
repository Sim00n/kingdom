package net.lsrp.kingdom.entity.mob;

import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.level.TileCoordinate;

public class Enemy extends Mob {

	public int id;
	private Sprite sprite;
	private int anim = 0;
	private boolean walking = false;
	public int xa = 0, ya = 0;
	public String username;
	public int xOffset, yOffset;
	
	public Enemy(int x, int y, String username) {
		this.x = x;
		this.y = y;
		this.username = username;
	}
	
	public Enemy(TileCoordinate tc, String username) {
		this.x = tc.x();
		this.y = tc.y();
		this.username = username;
	}
	
	@Override
	public void move(int xa, int ya) {
		if(xa > 0) dir = 1;
		if(xa < 0) dir = 3;
		if(ya > 0) dir = 2;
		if(ya < 0) dir = 0;
		
		x += xa;
		y += ya;
	}
	
	public void update() {
		if(anim < 7500) anim++;
		else anim = 0;
		
		if(xa != 0 || ya != 0) {
			move(xa, ya);
			walking = true;
		} else {
			walking = false;
		}
	}
	
	public void render(Screen screen){
		int flip = 0;
		
		if(dir == 0) {
			sprite = Sprite.enemyf;
			if(walking) {
				if(anim % 20 > 10) {
					sprite = Sprite.enemyf1;
				} else {
					sprite = Sprite.enemyf2;
				}
			}
		}
		else if(dir == 1) {
			sprite = Sprite.enemys;
			if(walking) {
				if(anim % 20 > 10) {
					sprite = Sprite.enemys1;
				} else {
					sprite = Sprite.enemys2;
				}
			}
		}
		else if(dir == 2) { 
			sprite = Sprite.enemyb;
			if(walking) {
				if(anim % 20 > 10) {
					sprite = Sprite.enemyb1;
				} else {
					sprite = Sprite.enemyb2;
				}
			}
		}
		else if(dir == 3) {
			 sprite = Sprite.enemys;
			 if(walking) {
				if(anim % 20 > 10) {
					sprite = Sprite.enemys1;
				} else {
					sprite = Sprite.enemys2;
				}
			}
			flip = 1;
		}
		
		screen.renderPlayer(x - 16, y - 16, sprite, flip);
	}
}
