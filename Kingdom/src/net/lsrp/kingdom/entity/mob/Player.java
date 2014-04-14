package net.lsrp.kingdom.entity.mob;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.entity.projectile.Projectile;
import net.lsrp.kingdom.entity.projectile.SlowProjectile;
import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.input.Chat;
import net.lsrp.kingdom.input.Keyboard;
import net.lsrp.kingdom.input.Mouse;
import net.lsrp.kingdom.level.TileCoordinate;

public class Player extends Mob {

	private static final long serialVersionUID = 1L;

	private Keyboard input;
	private Sprite sprite;
	private int anim = 0;
	private boolean walking = false;
	public int xa = 0, ya = 0;
	
	private int fireRate = 0;
	
	public Player(Keyboard input) {
		this.input = input;
		sprite = Sprite.playerf;
		fireRate = SlowProjectile.FIRE_RATE;
	}
	
	public Player(int x, int y, Keyboard input) {
		this.x = x;
		this.y = y;
		this.input = input;
		fireRate = SlowProjectile.FIRE_RATE;
	}
	
	public Player(TileCoordinate tc, Keyboard input) {
		this.x = tc.x();
		this.y = tc.y();
		this.input = input;
		fireRate = SlowProjectile.FIRE_RATE;
	}
	
	public void update() {
		if(fireRate > 0) fireRate--;
		
		if(anim < 7500) anim++;
		else anim = 0;
		
		if(input.up) ya--;
		if(input.down) ya++;
		if(input.left) xa--;
		if(input.right) xa++;
		
		if(xa != 0 || ya != 0) {
			if(!Chat.isTyping()) {
				move(xa, ya);
				walking = true;
			}
		} else {
			walking = false;
		}
		
		xa = 0;
		ya = 0;
		
		clear();
		updateShooting();
	}
	
	private void clear() {
		for(int i = 0; i < level.getProjectiles().size(); i++) {
			Projectile p = level.getProjectiles().get(i);
			if(p.isRemoved()) {
				level.getProjectiles().remove(i);
			}
		}
	}
	
	private void updateShooting() {
		if(Mouse.getButton() == 1 && fireRate <= 0) {
			double dx = Mouse.getX() - Game.getWindowWidth() / 2;
			double dy = Mouse.getY() - Game.getWindowHeight() / 2; 
			double dir = Math.atan2(dy, dx);
			shoot(x, y, dir);
			fireRate = SlowProjectile.FIRE_RATE;
		}
	}
	
	public void render(Screen screen){
		int flip = 0;
		
		if(dir == 0) {
			sprite = Sprite.playerf;
			if(walking) {
				if(anim % 20 > 10) {
					sprite = Sprite.playerf1;
				} else {
					sprite = Sprite.playerf2;
				}
			}
		}
		else if(dir == 1) {
			sprite = Sprite.players;
			if(walking) {
				if(anim % 20 > 10) {
					sprite = Sprite.players1;
				} else {
					sprite = Sprite.players2;
				}
			}
		}
		else if(dir == 2) { 
			sprite = Sprite.playerb;
			if(walking) {
				if(anim % 20 > 10) {
					sprite = Sprite.playerb1;
				} else {
					sprite = Sprite.playerb2;
				}
			}
		}
		else if(dir == 3) {
			 sprite = Sprite.players;
			 if(walking) {
				if(anim % 20 > 10) {
					sprite = Sprite.players1;
				} else {
					sprite = Sprite.players2;
				}
			}
			flip = 1;
		}
		
		screen.renderPlayer(x - 16, y - 16, sprite, flip);
		screen.renderPlayerTag(this);
		
	}
}
