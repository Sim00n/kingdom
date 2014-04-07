package net.lsrp.kingdom.network;
import java.io.IOException;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.input.Chat;
import net.lsrp.kingdom.network.Network.AddCharacter;
import net.lsrp.kingdom.network.Network.ChatMessage;
import net.lsrp.kingdom.network.Network.ConnectionEstablished;
import net.lsrp.kingdom.network.Network.Login;
import net.lsrp.kingdom.network.Network.MoveCharacter;
import net.lsrp.kingdom.network.Network.Register;
import net.lsrp.kingdom.network.Network.RegistrationRequired;
import net.lsrp.kingdom.network.Network.RemoveCharacter;
import net.lsrp.kingdom.network.Network.UpdateCharacter;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;


public class KryoClient {

	public Client client;
	public static ChatMessage chatmsg = null; 
	Login login = new Login();

	public static String IP = "";
	public static int PORT = -1;
	
	public KryoClient() {
		
		client = new Client();
		client.start();
		
		Network.register(client);
		
		client.addListener(new ThreadedListener(new Listener() {
			@Override
			public void connected(Connection c) {
				
			}
			
			@Override
			public void received(Connection connection, Object object) {
				if(object instanceof RegistrationRequired) {
					Register register = new Register();
					register.name = login.name;
					client.sendUDP(register);
				}
				
				if(object instanceof AddCharacter) {
					AddCharacter msg = (AddCharacter)object;
					if(!msg.character.name.equals(Game.username)) {
						Game.AddEnemy(msg.character);
						
						Chat.addToChat(createChatMessage(msg.character.name + " has connected to the game."));
					}
					return;
				}
				
				if(object instanceof UpdateCharacter) {
					Game.UpdateEnemy((UpdateCharacter)object);
					return;
				}
				
				if(object instanceof RemoveCharacter) {
					RemoveCharacter msg = (RemoveCharacter)object;
					Game.RemoveEnemy(msg.id);
					return;
				}
				
				if(object instanceof ConnectionEstablished) {
					ConnectionEstablished conne = (ConnectionEstablished)object;
					login.id = conne.id;
					Game.id = login.id;
					
					Chat.addToChat(createChatMessage("Connected to " + KryoClient.IP + ":" + KryoClient.PORT + "."));
					Chat.addToChat(createChatMessage("MOTD: " + conne.motd));
					
					return;
				}
				
				if(object instanceof ChatMessage) {
					ChatMessage msg = (ChatMessage)object;
					Chat.addToChat(msg);
					return;
				}
			}
			
			@Override
			public void disconnected(Connection c) {
				System.exit(0);
			}
		}));
		
		try {
			Chat.addToChat(createChatMessage("Connecting to " + KryoClient.IP + ":" + KryoClient.PORT + " ..."));
			
			client.connect(5000, KryoClient.IP, KryoClient.PORT, KryoClient.PORT);
			System.out.println(KryoClient.PORT);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				KryoClient.IP = "127.0.0.1";
				KryoClient.PORT = 54555;
				Chat.addToChat(createChatMessage("No server found at " + KryoClient.IP + ":" + KryoClient.PORT + "! Starting a local server."));
				
				new KryoServer();
				
				Chat.addToChat(createChatMessage("Connecting to " + KryoClient.IP + ":" + KryoClient.PORT + " ..."));
				
				client.connect(5000, KryoClient.IP, KryoClient.PORT, KryoClient.PORT);
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
						
						MoveCharacter msg = new MoveCharacter();
						msg.id = login.id;
						msg.x = Game.player.x;
						msg.y = Game.player.y;
						msg.dx = Game.player.xa;
						msg.dy = Game.player.ya;
						client.sendUDP(msg);
						Game.ping = client.getReturnTripTime();
						
						if(KryoClient.chatmsg != null) {
							client.sendTCP(chatmsg);
							KryoClient.chatmsg = null;
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
	
	public ChatMessage createChatMessage(String message) {
		ChatMessage cm = new ChatMessage();
		cm.authorName = "Client";
		cm.id = -1;
		cm.timestamp = System.currentTimeMillis();
		cm.message = message;
		return cm;
	}
}
