package net.lsrp.kingdom.entity.projectile;

import java.util.Random;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.entity.mob.Mob;
import net.lsrp.kingdom.entity.particle.ParticleEffect;
import net.lsrp.kingdom.entity.particle.ParticleEffect.PARTICLE_TYPE;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.level.Level;

public abstract class Projectile extends Entity {

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
	
	protected void collision(Entity e) {
		if(e instanceof Mob) {
			ParticleEffect pe = new ParticleEffect((int)x, (int)y, 50, 0xFF00FF00, 10, 2, PARTICLE_TYPE.LINEAR);
			Level.add(pe);
		} else {
			ParticleEffect pe = new ParticleEffect((int)x, (int)y, 50, 0xFF00FF00, 10, 2, PARTICLE_TYPE.SPATTER);
			Level.add(pe);
		}
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public int getSpriteSize() {
		return sprite.SIZE;
	}
}
