package net.lsrp.kingdom.entity.projectile;

import java.util.Random;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.level.Level;

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
	public void update(double delta) {
		super.update(delta);
	}
	
	protected void move() {
		
	}
	
	protected void collision() {
		ParticleEffect pe = new ParticleEffect((int)x, (int)y, 10, 0xFFAABBCC, 40, 2500, 10, true);
		Level.add(pe);
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public int getSpriteSize() {
		return sprite.SIZE;
	}
}
