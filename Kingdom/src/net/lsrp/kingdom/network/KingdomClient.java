package net.lsrp.kingdom.network;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.entity.mob.Enemy;
import net.lsrp.kingdom.entity.projectile.Projectile;
import net.lsrp.kingdom.entity.projectile.SlowProjectile;
import net.lsrp.kingdom.input.Chat;
import net.lsrp.kingdom.level.Level;
import net.lsrp.kingdom.network.KingdomNetwork.AddCharacter;
import net.lsrp.kingdom.network.KingdomNetwork.ChatMessage;
import net.lsrp.kingdom.network.KingdomNetwork.ConnectionEstablished;
import net.lsrp.kingdom.network.KingdomNetwork.Login;
import net.lsrp.kingdom.network.KingdomNetwork.ProjectileMessage;
import net.lsrp.kingdom.network.KingdomNetwork.Register;
import net.lsrp.kingdom.network.KingdomNetwork.RegistrationRequired;
import net.lsrp.kingdom.network.KingdomNetwork.RemoveCharacter;
import net.lsrp.kingdom.network.KingdomNetwork.UpdateCharacter;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;


public class KingdomClient {

	public Client client;
	Login login = new Login();

	public static String IP = "";
	public static int PORT = -1;
	
	public static ChatMessage chatmsg = null;
	public static List<Projectile> projectileUpStream = new ArrayList<Projectile>();
	
	public KingdomClient() {
		
		client = new Client();
		client.start();
		
		KingdomNetwork.register(client);
		
		client.addListener(new ThreadedListener(new Listener() {
			@Override
			public void connected(Connection c) {
				
			}
			
			@Override
			public void received(Connection connection, Object object) {
				Game.ping = connection.getReturnTripTime();
				
				if(object instanceof RegistrationRequired) {
					Register register = new Register();
					register.name = login.name;
					client.sendUDP(register);
				}
				
				if(object instanceof AddCharacter) {
					AddCharacter msg = (AddCharacter)object;
					if(!msg.character.name.equals(Game.username)) {
						Enemy.AddEnemy(msg.character);
						
						Chat.addToChat(createChatMessage(msg.character.name + " has connected to the game."));
					}
					return;
				}
				
				if(object instanceof UpdateCharacter) {
					Enemy.UpdateEnemy((UpdateCharacter)object);
					return;
				}
				
				if(object instanceof RemoveCharacter) {
					RemoveCharacter msg = (RemoveCharacter)object;
					Enemy.RemoveEnemy(msg.id);
					return;
				}
				
				if(object instanceof ConnectionEstablished) {
					ConnectionEstablished conne = (ConnectionEstablished)object;
					login.id = conne.id;
					Game.id = login.id;
					
					Chat.addToChat(createChatMessage("Connected to " + KingdomClient.IP + ":" + KingdomClient.PORT + "."));
					Chat.addToChat(createChatMessage("MOTD: " + conne.motd));
					
					return;
				}
				
				if(object instanceof ChatMessage) {
					ChatMessage msg = (ChatMessage)object;
					Chat.addToChat(msg);
					return;
				}
				
				if(object instanceof ProjectileMessage) {
					ProjectileMessage msg = (ProjectileMessage)object;
					if(msg.type == 0) return;
					
					if(msg.type == 1) {
						Projectile p = new SlowProjectile((int)msg.x, (int)msg.y, msg.angle, msg.originator);
						Level.add(p);
						//System.out.println("New Projectile at: " + msg.x + " | " + msg.y + " | " + msg.angle);
					}
					return;
				}
			}
			
			@Override
			public void disconnected(Connection c) {
				System.exit(0);
			}
		}));
		
		try {
			Chat.addToChat(createChatMessage("Connecting to " + KingdomClient.IP + ":" + KingdomClient.PORT + " ..."));
			client.connect(5000, KingdomClient.IP, KingdomClient.PORT, KingdomClient.PORT);
		} catch (IOException e) {
			//e.printStackTrace();
			try {
				KingdomClient.IP = "127.0.0.1";
				KingdomClient.PORT = 54555;
				Chat.addToChat(createChatMessage("No server found at " + KingdomClient.IP + ":" + KingdomClient.PORT + "! Starting a local server."));
				
				new KingdomServer();
				
				Chat.addToChat(createChatMessage("Connecting to " + KingdomClient.IP + ":" + KingdomClient.PORT + " ..."));
				
				client.connect(5000, KingdomClient.IP, KingdomClient.PORT, KingdomClient.PORT);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		login.name = Game.username;
		client.sendUDP(login);
		
		Thread ct = new Thread() {
			@Override
			public void run() {
				
				long lastTime = System.nanoTime();
				long timer = System.currentTimeMillis();
				final double ns = 1000000000.0 / 60.0;
				double delta = 0;
				int ticks = 0; 
				
				while(true)
				{
					if(Game.player != null)
					{
						long now = System.nanoTime();
						delta += (now - lastTime) / ns;
						lastTime = now;
						
						while(delta >= 1) {
							ticks++;
							delta--;
						}
						
						if(System.currentTimeMillis() - timer > 1000) {
							timer += 1000;
							Game.network_ticks = ticks;
							ticks = 0;
						}
						
						UpdateCharacter msg = new UpdateCharacter();
						msg.id = login.id;
						msg.x = Game.player.x;
						msg.y = Game.player.y;
						msg.dx = Game.player.xa;
						msg.dy = Game.player.ya;
						msg.health = Game.player.getHealth();
						msg.dir = Game.player.dir;
						msg.walking = Game.player.walking;
						client.sendUDP(msg);
						
						if(KingdomClient.chatmsg != null) {
							client.sendTCP(chatmsg);
							KingdomClient.chatmsg = null;
						}
						
						Iterator<Projectile> ei = projectileUpStream.iterator();
						while(ei.hasNext()) {
							ProjectileMessage em = new ProjectileMessage();
							Projectile next = ei.next();
							
							em.x = next.x;
							em.y = next.y;
							em.angle = next.angle;
							em.originator = Game.id;
							
							if(next instanceof SlowProjectile)
								em.type = 1;
							
							client.sendTCP(em);
							ei.remove();
						}
					}
					
					try {
						Thread.sleep(Game.network_frames);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
			}
		};
		ct.start();
	}
	
	public static ChatMessage createChatMessage(String message) {
		ChatMessage cm = new ChatMessage();
		cm.authorName = "Client";
		cm.id = -1;
		cm.timestamp = System.currentTimeMillis();
		cm.message = message;
		return cm;
	}
}
