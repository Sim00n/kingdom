package net.lsrp.kingdom.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

	private boolean[] keys = new boolean[65563];
	public boolean up, down, left, right, tab, tab2;

	public void update() {
		up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
		tab = keys[KeyEvent.VK_TAB];
		tab2 = keys[KeyEvent.VK_Q];
		
		
		if((tab || tab2) && !Chat.playerlist && !Chat.isTyping())
			Chat.playerlist = true;
		if(!(tab || tab2) && Chat.playerlist)
			Chat.playerlist = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
		
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if (Chat.isTyping()) {
				Chat.deleteChar();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (Chat.isTyping()) {
				Chat.endTyping();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (Chat.isTyping()) {
				Chat.send();
			}
			Chat.endTyping();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 't' || e.getKeyChar() == 'T') {
			if (!Chat.isTyping()) {
				Chat.type();
			} else {
				Chat.addChar(e.getKeyChar());
			}
		} else {
			if (e.getKeyCode() == 0 && e.getKeyChar() != '\n' && e.getKeyChar() != '\b')
				Chat.addChar(e.getKeyChar());
		}
	}

}
