package net.lsrp.kingdom.entity.projectile;

import java.io.Serializable;
import java.util.Random;

import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.graphics.Sprite;

public abstract class Projectile extends Entity implements Serializable {

	private static final long serialVersionUID = 1L;
	protected double x, y;
	protected double nx, ny;
	protected final int xOrigin, yOrigin;
	protected double angle;
	protected double distance;
	Sprite sprite;
	protected double speed, range, damage;
	
	protected final Random random = new Random();
	
	public Projectile(int x, int y, double dir) {
		xOrigin = x;
		yOrigin = y;
		this.x = x;
		this.y = y;
		angle = dir;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
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
