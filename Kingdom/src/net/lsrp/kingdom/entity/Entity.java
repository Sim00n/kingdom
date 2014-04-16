package net.lsrp.kingdom.entity;

import java.io.Serializable;
import java.util.Random;

import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.level.Level;

public abstract class Entity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int x, y;
	protected int ix, iy;
	private boolean removed = false;
	protected Level level;
	protected final Random random = new Random();
	
	public void update(double delta) {
		
	}
	
	public void render(Screen screen) {
	}
	
	public void remove() {
		//Remove from level
		removed = true;
		Level.remove(this);
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void init(Level level) {
		this.level = level;
	}
}
