package net.lsrp.kingdom.entity.mob;

import java.util.ArrayList;
import java.util.List;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.level.TileCoordinate;
import net.lsrp.kingdom.network.KingdomCharacter;
import net.lsrp.kingdom.network.KingdomNetwork.UpdateCharacter;

public class Enemy extends Mob {

	private static final long serialVersionUID = 1L;

	public static List<Enemy> enemies = new ArrayList<Enemy>();
	
	public int id;
	private int anim = 0;
	private boolean walking = false;
	public int xa = 0, ya = 0;
	public int xOffset, yOffset;
	
	public Enemy(int x, int y, String username) {
		this.x = x;
		this.y = y;
		name = username;
		sprite = Sprite.enemyf;
	}
	
	public Enemy(TileCoordinate tc, String username) {
		this.x = tc.x();
		this.y = tc.y();
		name = username;
		sprite = Sprite.enemyf;
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
		screen.renderPlayerTag(this);
	}
	
	public static void AddEnemy(KingdomCharacter character) {
		Enemy enemy;
		if(character.x != 0 && character.y != 0)
			enemy = new Enemy(character.x, character.y, character.name);
		else
			enemy = new Enemy(Mob.defaultSpawn, character.name);
		enemy.id = character.id;
		
		if(character.id != Game.id && !character.name.equals(Game.username)) {
			enemies.add(enemy);
		}
		return;
	}
	
	public static void RemoveEnemy(int id) {
		for(Enemy enemy : enemies) {
			if(enemy.id == id) {
				enemies.remove(enemy);
				return;
			}
		}
	}
	
	public static void UpdateEnemy(UpdateCharacter character) {
		for(Enemy enemy : enemies) {
			if(character.id != Game.id) {
				if(enemy.id == character.id) {
					enemy.x = character.x;
					enemy.y = character.y;
					enemy.xa = character.dx;
					enemy.ya = character.dy;
					enemy.setHealth(character.health);
					return;
				}
			}
		}
	}
}
