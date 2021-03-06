package net.lsrp.kingdom.entity.mob;

import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.entity.projectile.Projectile;
import net.lsrp.kingdom.entity.projectile.SlowProjectile;
import net.lsrp.kingdom.graphics.Sprite;
import net.lsrp.kingdom.level.Level;
import net.lsrp.kingdom.level.TileCoordinate;
import net.lsrp.kingdom.network.KingdomClient;

public abstract class Mob extends Entity {

	protected Sprite sprite = Sprite.enemyf;
	public int dir = 0;
	protected boolean moving = false;
	protected final double maxHealth = 100.0;
	protected double health = maxHealth;
	public String name = "Mob";
	public static final TileCoordinate defaultSpawn = Level.level.getSpawn();
	
	public void move(int xa, int ya) {
		if(xa != 0 && ya != 0) {
			move(xa, 0);
			move(0, ya);
			return;
		}
		
		if(xa > 0) dir = 1;
		if(xa < 0) dir = 3;
		if(ya > 0) dir = 2;
		if(ya < 0) dir = 0;
		
		if(!collision(xa, ya)) {
			x += xa;
			y += ya;
		}
	}
	
	public void update() {
		
	}
	
	protected void shoot(int x, int y, double dir) {
		Projectile p = new SlowProjectile(x, y, dir);
		level.addProjectile(p);
		KingdomClient.projectileUpStream.add(p);
	}
	
	private boolean collision(int xa, int ya) {
		boolean solid = false;
		
		for(int c = 0; c < 4; c++) {
			int xt = ((x + xa) + c % 2 * 14 - 8) >> 4;
			int xy = ((y + ya) + c / 2 * 12 + 3) >> 4;
			if(level.getTile(xt, xy).solid()) {
				solid = true;
				health = health -1;
				if(health < 0)
					health = 0;
			}
		}
		return solid;
	}
	
	public void render() {
		
	}
	
	public void death() {
		
	}
	
	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void hit(double damage) {
		health -= damage;
	}
	
	public boolean projectileCollision(Projectile p) {
		
		if(!p.isRemoved())
			if(p.y + p.sprite.SIZE > y && p.y < y + sprite.SIZE)
				if(p.x + p.sprite.SIZE > x && p.x < x + sprite.SIZE)
					return true;
		return false;
	}
}
