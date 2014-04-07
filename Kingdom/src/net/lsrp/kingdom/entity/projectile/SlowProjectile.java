package net.lsrp.kingdom.entity.projectile;

import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.graphics.Sprite;

public class SlowProjectile extends Projectile {

	private static final long serialVersionUID = 1L;
	public static final int FIRE_RATE = 15;
	
	public SlowProjectile(int x, int y, double dir) {
		super(x, y, dir);
		range = 100;
		speed = 4;
		damage = 20;
		
		sprite = Sprite.slow_projectile;
				
		nx = Math.cos(angle) * speed;
		ny = Math.sin(angle) * speed;
	}

	@Override
	public void update() {
		move();
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
		screen.renderProjectile((int) x - 12, (int) y - 2, this);
	}
}
