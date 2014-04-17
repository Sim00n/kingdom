package net.lsrp.kingdom.entity.particle;

import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.graphics.Screen;

public class Particle extends Entity {
	
	public int color, xOrigin, yOrigin, range, speed;
	public double dx, dy;
	public double dir;
	public double startTime;
	
	public Particle(int x, int y, int color, int range, int speed) {
		this.x = x;
		this.y = y;
		this.xOrigin = x;
		this.yOrigin = y;
		this.color = color;
		this.range = range;
		this.speed = speed;
		
		startTime = System.currentTimeMillis();
		
		dx = random.nextDouble()*speed - random.nextDouble()*(speed-1);
		dy = random.nextDouble()*speed - random.nextDouble()*(speed-1); 
		dir = Math.atan2(dy, dx);
	}
	
	@Override
	public void render(Screen screen) {
		if(!isRemoved())
			screen.renderParticle(x, y, color);
	}
	
	@Override
	public void update(double delta) {
		move();
		
		if(System.currentTimeMillis() - startTime > 1000)
			remove();
	}
	
	protected void move() {
		x += dx;
		y += dy;
		if(distance() > range) remove();
	}
	
	private double distance() {
		double dist = 0;
		dist = Math.sqrt(Math.abs(Math.pow(xOrigin - x, 2) + Math.pow(yOrigin - y, 2)));
		return dist;
	}
}
