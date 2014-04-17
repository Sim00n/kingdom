package net.lsrp.kingdom.entity.particle;

import net.lsrp.kingdom.entity.Entity;
import net.lsrp.kingdom.graphics.Screen;

public class ParticleEffect extends Entity {

	public int radius, color, count, speed;
	public PARTICLE_TYPE type;
	public Particle[] particles;
	
	public enum PARTICLE_TYPE {
		LINEAR, SPATTER
	}
	
	public ParticleEffect(int x, int y, int radius, int color, int count, int speed, PARTICLE_TYPE type) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
		this.count = count;
		this.speed = speed;
		this.type = type;
		
		particles = new Particle[count];
		generate();
	}
	
	public void generate() {
		for(int i = 0; i < particles.length; i++) {
			if(type == PARTICLE_TYPE.LINEAR)
				particles[i] = new Particle(x, y, color, radius, speed);
			else if(type == PARTICLE_TYPE.SPATTER)
				particles[i] = new SpatterParticle(x, y, color, radius, speed);
		}
	}
	
	@Override
	public void update(double delta) {
		for(int i = 0; i < particles.length; i++) {
			particles[i].update(delta);
		}
	}
	
	@Override
	public void render(Screen screen) {
		for(int i = 0; i < particles.length; i++) {
			particles[i].render(screen);
		}
	}
	
}
