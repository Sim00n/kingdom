package net.lsrp.kingdom;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.lsrp.kingdom.entity.mob.Enemy;
import net.lsrp.kingdom.entity.mob.Player;
import net.lsrp.kingdom.graphics.Screen;
import net.lsrp.kingdom.input.Chat;
import net.lsrp.kingdom.input.Keyboard;
import net.lsrp.kingdom.input.Mouse;
import net.lsrp.kingdom.level.Level;
import net.lsrp.kingdom.level.TileCoordinate;
import net.lsrp.kingdom.network.KryoCharacter;
import net.lsrp.kingdom.network.KryoClient;
import net.lsrp.kingdom.network.Network.UpdateCharacter;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static int width = 300;
	private static int height = width / 16 * 9;
	private static int scale = 3;
	public static String title = "Kingdom";
	public int hud_frames = 0;
	public int hud_ticks = 0;
	public static int network_frames = (int)(1000/60);
	public static int network_ticks = 0;
	public static int ping = 0;
	
	public static String username;
	public static int id;
	
	private Thread thread;
	private JFrame frame;
	private Keyboard key;
	private Mouse mouse;
	private Level level;
	public static Player player;
	public static List<Enemy> enemies = new ArrayList<Enemy>();
	private boolean running = false;
	
	public static Screen screen;
	
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	
	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		
		screen = new Screen(width, height);
		frame = new JFrame();
		key = new Keyboard();
		mouse =  new Mouse();
		level = Level.spawn;
		player = new Player(new TileCoordinate(20, 65), key);
		player.init(level);
		//enemies = new Enemy[32];
		
		addKeyListener(key);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}
	
	public static void AddEnemy(KryoCharacter character) {
		Enemy enemy = new Enemy(character.x, character.y, character.name);
		enemy.id = character.id;
		if(character.id != id && character.name != username) {
			enemies.add(enemy);
			System.out.println("Dodaje enemy: " + character.name);
		}
		System.out.println("C: " + character.id);
		System.out.println("C: " + character.name);
		System.out.println("C: " + character.x);
		System.out.println("C: " + character.y);
		System.out.println("C: " + username);
		return;
	}
	
	public static void RemoveEnemy(int id) {
		for(Enemy enemy : enemies) {
			if(enemy.id == id) {
				enemies.remove(enemy);
				System.out.println("Usuwam enemy: " + id);
				return;
			}
		}
	}
	
	public static void UpdateEnemy(UpdateCharacter character) {
		for(Enemy enemy : enemies) {
			if(character.id != Game.id) {
				if(enemy.id == character.id) {
					enemy.x = character.x;
					enemy.y = character.y;
					enemy.xa = character.dx;
					enemy.ya = character.dy;
					return;
				}
			}
		}
	}
	
	public static int getWindowWidth() {
		return width * scale;
	}
	
	public static int getWindowHeight() {
		return height * scale;
	}
	
	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
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
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		int ticks = 0; 
		
		requestFocus();
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			while(delta >= 1) {
				tick();
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
	
	public void tick() {
		key.update();
		player.update();
		level.update();
		
		for(Enemy e : enemies) {
			if(e != null) {
				e.update();
			}
		}	
				/*
		if(enemiesPackage != null) {
			for(int i = 0; i < enemiesPackage.length; i++) {
				if(enemiesPackage[i] != null) {
					DataPackage12345 dp = enemiesPackage[i];
					if(enemies[i] == null) {
						enemies[i] = new Enemy(new TileCoordinate(dp.x, dp.y), dp.username);
					} else {
						enemies[i].x = dp.x;
						enemies[i].y = dp.y;
						enemies[i].xa = dp.xa;
						enemies[i].ya = dp.ya;
						enemies[i].dir = dp.dir;
						enemies[i].xOffset = dp.xOffset;
						enemies[i].yOffset = dp.yOffset;
						enemies[i].username = dp.username;
						for(int j = 0; j < dp.entities.size(); j++) {
							level.add(dp.entities.get(j));
						}
						for(int j = 0; j < dp.projectiles.size(); j++) {
							level.add(dp.projectiles.get(j));
						}
					}
				}
			}
		}*/
		
		Chat.update();
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		screen.clear();
		
		int xScroll = player.x - screen.width/2;
		int yScroll = player.y - screen.height/2;
		level.render(xScroll, yScroll, screen);
		
		for(Enemy e : enemies) {
			if(e != null) {
				e.render(screen);
			}
		}
		
		player.render(screen);
		
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", 0, 15));
		g.drawString("Player X: " + player.x + " ("+player.x/16 +")" + ", Y: " + player.y + " (" + player.y/16 + ")", 10, 20);
		g.drawString("Frames: " + hud_frames + " | Ticks: " + hud_ticks, 10, 40);
		g.drawString("Tiles: " + level.getTiles().length, 10, 60);
		
		if(key.tab || key.tab2) {
			g.setColor(new Color(39, 38, 46, 70));
			g.fillRect(getWidth() - 170 , 10, 150, 200);
			g.setColor(new Color(255, 255, 255));
			int lastH = 20 + 10;
			for(Enemy enemy : enemies) {
				if(enemy != null) {
					g.drawString(enemy.id + "  " + enemy.username, getWidth() - 170 + 10, lastH);
					lastH += 20;
				}
			}
		}
		
		Chat.render(g);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// nothin for now
			}
		});
		
		KryoClient.IP = JOptionPane.showInputDialog("IP: ");
		KryoClient.PORT = new Integer(JOptionPane.showInputDialog("Port: "));
		username = JOptionPane.showInputDialog("Username: ");		
		//client = new Client12345("178.63.33.130", 7788, username);
		//if(client.connected) {
			new KryoClient();
			Game game = new Game();
			game.frame.setResizable(false);
			game.frame.setTitle("Kingdom");
			game.frame.add(game);
			game.frame.pack();
			game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			game.frame.setLocationRelativeTo(null);
			game.frame.setVisible(true);		
			game.start();
		//}
	}
}