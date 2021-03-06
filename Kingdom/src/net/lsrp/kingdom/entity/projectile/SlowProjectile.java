package net.lsrp.kingdom.entity.projectile;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.entity.mob.Enemy;
import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.level.Level;

public class SlowProjectile extends Projectile {

	public static final int FIRE_RATE = 15;
	
	public SlowProjectile(int x, int y, double dir) {
		super(x, y, dir);
		range = 100;
		speed = 4;
		damage = 5;
		
		sprite = Sprite.slow_projectile;
				
		nx = Math.cos(angle) * speed;
		ny = Math.sin(angle) * speed;
	}
	
	public SlowProjectile(int x, int y, double dir, int originator) {
		super(x, y, dir);
		range = 100;
		speed = 4;
		damage = 5;
		
		sprite = Sprite.slow_projectile;
				
		nx = Math.cos(angle) * speed;
		ny = Math.sin(angle) * speed;
		
		this.originator = originator;
	}

	@Override
	public void update(double delta) {
		move();
		if(Game.player.projectileCollision(this) && Game.id != originator) {
			Game.player.hit(damage);
			collision(Game.player);
			this.remove();
			return;
		}
		for(Enemy enemy : Enemy.enemies) {
			if(enemy.projectileCollision(this) && enemy.id != originator) {
				enemy.hit(damage);
				collision(enemy);
				this.remove();
				return;
			}
		}
		
		if(Level.level.getTile((int)x >> 4, (int)y >> 4).solid()) {
			collision(null);
			this.remove();
			return;
		}
	}
	
	protected void move() {
		x += nx;
		y += ny;
		if(distance() > range) remove();
	}
	
	private double distance() {
		double dist = 0;
		dist = Math.sqrt(Math.abs(Math.pow(xOrigin - x, 2) + Math.pow(yOrigin - y, 2)));
		return dist;
	}
	
	@Override
	public void render(Screen screen) {
		if(!isRemoved())
			screen.renderProjectile((int) x - 12, (int) y - 2, this);
	}
}
