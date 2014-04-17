package net.lsrp.kingdom.entity.particle;

public class SpatterParticle extends Particle {

	public SpatterParticle(int x, int y, int color, int range, int speed) {
		super(x, y, color, range, speed);
	}
	
	@Override
	public void update(double delta) {
		super.update(delta);
		
		dx = random.nextDouble()*speed - random.nextDouble()*(speed-1);
		dy = random.nextDouble()*speed - random.nextDouble()*(speed-1); 
	}
}
