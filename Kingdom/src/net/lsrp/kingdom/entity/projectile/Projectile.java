package net.lsrp.kingdom.entity.projectile;

import java.util.Random;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.graphics.Sprite;

public abstract class Projectile extends Entity {

	private static final long serialVersionUID = 1L;

	public double x, y;
	public double nx, ny;
	public final int xOrigin, yOrigin;
	public double angle;
	public double distance;
	public Sprite sprite;
	public double speed, range, damage;
	public int originator;
	
	protected final Random random = new Random();
	
	public Projectile(int x, int y, double dir) {
		xOrigin = x;
		yOrigin = y;
		this.x = x;
		this.y = y;
		angle = dir;
		originator = Game.id;
	}

	@Override
	public void update() {
		super.update();
	}
	
	protected void move() {
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public int getSpriteSize() {
		return sprite.SIZE;
	}
}
