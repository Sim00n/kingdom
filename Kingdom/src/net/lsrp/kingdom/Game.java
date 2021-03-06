package net.lsrp.kingdom;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.lsrp.kingdom.entity.mob.Enemy;
import net.lsrp.kingdom.entity.mob.Mob;
import net.lsrp.kingdom.entity.mob.Player;
import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.input.Chat;
import net.lsrp.kingdom.input.Keyboard;
import net.lsrp.kingdom.input.Mouse;
import net.lsrp.kingdom.level.Level;
import net.lsrp.kingdom.network.KingdomClient;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;
	
	// Settings
	private static int width = 300;
	private static int height = width / 16 * 9;
	private static int scale = 3;
	private static double TICKS = 60.0;
	
	// Name
	public static String title = "Kingdom";
	
	// Client stats
	public int hud_frames = 0;
	public int hud_ticks = 0;
	public static int network_frames = (int)(1000/60);
	public static int network_ticks = 0;
	public static int ping = 0;
	
	// Client info
	public static String username;
	public static int id;
	
	// Engine components
	private Screen screen;
	private Thread thread;
	private JFrame frame;
	private Keyboard key;
	private Mouse mouse;
	
	// Players
	public static Player player;
	
	// Runtime components
	private boolean running = false;
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	
	// Constructor
	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		
		screen = new Screen(width, height);
		frame = new JFrame();
		key = new Keyboard();
		mouse =  new Mouse();
		Level.level = Level.spawn;
		
		player = new Player(Mob.defaultSpawn, key);
		player.init(Level.level);
		
		addKeyListener(key);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}
		
	public synchronized void start() {
		thread = new Thread(this, "Display");
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / TICKS;
		double delta = 0;
		int frames = 0;
		int ticks = 0; 
		
		requestFocus();
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			while(delta >= 1) {
				tick(delta * (1000.0/TICKS));
				ticks++;
				delta--;
			}
			render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(title+ " | Frames: " + frames + " | Ticks: " + ticks + " | Network ticks: " + network_ticks + " | Ping: " + ping);
				hud_frames = frames;
				hud_ticks = ticks;
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick(double delta) {
		key.update();
		player.update();
		Level.level.update(delta);
		Chat.update();
		
		for(Enemy enemy : Enemy.enemies)
			enemy.update();
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}

		// Clear the screen cache
		screen.clear();
		
		// Calculate screen placement and render level
		int xScroll = player.x - screen.width/2;
		int yScroll = player.y - screen.height/2;
		Level.level.render(xScroll, yScroll, screen);
		
		// Render enemies
		for(Enemy e : Enemy.enemies) {
			if(e != null) {
				e.render(screen);
			}
		}
		
		// Render player
		player.render(screen);
		
		// Copy rendered screen pixels into actual pixels array
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}
		
		// Draw to screen
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", 0, 11+scale));
		
		// Print debug info
		g.drawString("Player X: " + player.x + " ("+player.x/16 +")" + ", Y: " + player.y + " (" + player.y/16 + ")", 10, 20);
		g.drawString("Frames: " + hud_frames + " | Ticks: " + hud_ticks, 10, 40);
		g.drawString("Tiles: " + Level.level.getTiles().length, 10, 60);
		
		// Render player name
		//g.drawString(username, player.x - Screen.xOffset + (getWidth()/10)*3 - username.length(), player.y - Screen.yOffset + (getHeight()/10)*2);
		g.drawString(username, (player.x - Screen.xOffset)*scale - username.length()*4, (player.y - Screen.yOffset)*scale - 22*scale);
		
		// Render enemy names
		for(Enemy e : Enemy.enemies) {
			if(e != null) {
				int xp = (Game.width * scale)/2 - (player.x - e.x)*scale - e.getName().length()*4;
				int yp = (Game.height * scale)/2 - (player.y - e.y)*scale - (22*scale);
				g.drawString(e.getName(), xp, yp);
			}
		}
		
		// Chat and player list rendering
		Chat.render(g);
		
		g.dispose();
		bs.show();
	}
	
	/*
	 * Getters and setters, main ===============================
	 */
	
	public static int getWindowWidth() {
		return width * scale;
	}
	
	public static int getWindowHeight() {
		return height * scale;
	}
	
	public static void main(String[] args) {
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// nothin for now
			}
		});
		
		if(DEBUG) {
			KingdomClient.IP = "127.0.0.1";
			KingdomClient.PORT = 54555;
			username = "Sim00n";
		} else {
			try {
				KingdomClient.IP = JOptionPane.showInputDialog("IP: ");
				KingdomClient.PORT = new Integer(JOptionPane.showInputDialog("Port: "));
				username = JOptionPane.showInputDialog("Username: ");
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Nie wpisałeś poprawnego IP, portu, lub nazwy użytkownika.");
				System.exit(0);
			}
			
			if(KingdomClient.IP == null || KingdomClient.PORT < 0 || KingdomClient.PORT > 65563 || username == null) {
				JOptionPane.showMessageDialog(null, "Nie wpisałeś poprawnego IP, portu, lub nazwy użytkownika.f");
				System.exit(0);
			}
		}
		
		new KingdomClient();
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.setTitle("Kingdom");
		game.frame.add(game);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);		
		game.start();
	}
}
