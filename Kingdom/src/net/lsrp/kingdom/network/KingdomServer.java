package net.lsrp.kingdom.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import net.lsrp.kingdom.network.KingdomNetwork.AddCharacter;
import net.lsrp.kingdom.network.KingdomNetwork.ChatMessage;
import net.lsrp.kingdom.network.KingdomNetwork.ConnectionEstablished;
import net.lsrp.kingdom.network.KingdomNetwork.Login;
import net.lsrp.kingdom.network.KingdomNetwork.ProjectileMessage;
import net.lsrp.kingdom.network.KingdomNetwork.Register;
import net.lsrp.kingdom.network.KingdomNetwork.RegistrationRequired;
import net.lsrp.kingdom.network.KingdomNetwork.RemoveCharacter;
import net.lsrp.kingdom.network.KingdomNetwork.UpdateCharacter;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class KingdomServer {

	Server server;
	HashSet<KingdomCharacter> loggedIn = new HashSet<KingdomCharacter>();
	
	public static final int PORT = 54555;
	public static final String MOTD = "Wiadomo�� powitalna z serwera";
	
	public KingdomServer() throws IOException {
		server = new Server() {
			protected Connection newConnection() {
				return new CharacterConnection();
			}
		};
		
		KingdomNetwork.register(server);
		
		server.addListener(new Listener() {
			@Override
			public void received(Connection c, Object object) {
				CharacterConnection connection = (CharacterConnection)c;
				connection.cHandle = connection;
				KingdomCharacter character = connection.character;
				
				if(object instanceof Login) {
					if(character != null) return;
				
					String name = ((Login)object).name;
					if(!isValid(name)) {
						log("[error] Invalid name connection. Name: " + name);
						c.close();
						return;
					}
					
					for(KingdomCharacter other : loggedIn) {
						if(other.name.equals(name)) {
							log("[error] Double login connection. Name: " + name);
							disconnectCharacter(name);
							c.close();
							return;
						}
					}
					
					character = loadCharacter(name);
					
					if(character == null) {
						log("[info] Registration required. Name: " + name);
						c.sendTCP(new RegistrationRequired());
						return;
					}
					
					loggedIn(connection, character);
					ConnectionEstablished conne = new ConnectionEstablished();
					conne.motd = KingdomServer.MOTD;
					conne.id = character.id;
					c.sendTCP(conne);
					
					log("[join] " + name + " has connected to the server.");
							
					return;
				}
				
				
				if(object instanceof Register) {
					if(character != null) return;
					
					Register register = (Register)object;
					
					if(!isValid(register.name)) {
						log("[error] Trying to register invalid name. Name: " + register.name);
						c.close();
						return;
					}
					
					if(loadCharacter(register.name) != null) {
						log("[error] Trying to register a name that already exists. Name: " + register.name);
						c.close();
						return;
					}
					
					character = new KingdomCharacter();
					character.name = register.name;
					character.dx = 0;
					character.dy = 0;
					character.health = 100;
					character.id = saveCharacter(character); 
					if(character.id == -1) {
						log("[error] Unable to save character. Internal error!!! Name: " + character.name);
						c.close();
						return;
					}
					
					loggedIn(connection, character);
					
					ConnectionEstablished conne = new ConnectionEstablished();
					conne.motd = KingdomServer.MOTD;
					conne.id = character.id;
					c.sendTCP(conne);
										
					log("[join] " + character.name + " has connected after registration.");
					return;
				}
				
				
				if(object instanceof UpdateCharacter) {
					if(character == null) return;
					
					UpdateCharacter msg = (UpdateCharacter)object;
					character.x = msg.x;
					character.y = msg.y;
					character.dx = msg.dx;
					character.dy = msg.dy;
					character.health = msg.health;
					character.dir = msg.dir;
					character.walking = msg.walking;
					if(saveCharacter(character) == -1) {
						connection.close();
						return;
					}
					
					server.sendToAllUDP(msg);
					return;
				}
				
				if(object instanceof ChatMessage) {
					//if(character != null) return;
					
					ChatMessage chat = (ChatMessage)object;
					server.sendToAllTCP(chat);
					
					log("[msg] " + chat.authorName + ": " + chat.message);
					return;
				}
				
				if(object instanceof ProjectileMessage) {
					ProjectileMessage pm = (ProjectileMessage)object;
					server.sendToAllExceptTCP(connection.getID(), pm);
					return;
				}
			}
			
			private boolean isValid(String value) {
				if(value == null) return false;
				value = value.trim();
				if(value.length() == 0) return false;
				return true;
			}
			
			@Override
			public void disconnected(Connection c) {
				CharacterConnection connection = (CharacterConnection)c;
				if(connection.character != null) {
					RemoveCharacter rmc = new RemoveCharacter();
					rmc.id = connection.character.id;
					server.sendToAllTCP(rmc);
					disconnectCharacter(connection.character.name);
					log("[part] " + connection.character.name + " has disconnected from the server.");
				}
			}
		});
		
		//server.bind(Network.port, Network.port);
		server.bind(KingdomServer.PORT, KingdomServer.PORT);
		server.start();
	}
	
	public void loggedIn(CharacterConnection c, KingdomCharacter character) {
		c.character = character;
		
		for(KingdomCharacter other : loggedIn) {
			AddCharacter addCharacter = new AddCharacter();
			addCharacter.character = other;
			c.sendTCP(addCharacter);
		}
		
		loggedIn.add(character);
		
		AddCharacter addCharacter = new AddCharacter();
		addCharacter.character = character;
		server.sendToAllExceptTCP(c.getID(), addCharacter);
	}
	
	public void disconnectCharacter(String name) {
		Iterator<KingdomCharacter> iter = loggedIn.iterator();
		while(iter.hasNext()) {
			if(iter.next().name.equals(name)) {
				iter.remove();
			}
		}
	}
	
	public int saveCharacter(KingdomCharacter character) {
		File file = new File("characters", character.name.toLowerCase());
		file.getParentFile().mkdirs();
		
		if(character.id == 0) {
			String[] children = file.getParentFile().list();
			if(children == null) return -1;
			character.id = children.length + 1;
		}
		
		DataOutputStream output = null;
		try {
			output = new DataOutputStream(new FileOutputStream(file));
			output.writeInt(character.id);
			output.writeInt(character.x);
			output.writeInt(character.y);
			output.writeInt(character.dx);
			output.writeInt(character.dy);
			return character.id;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				output.close();
			} catch (IOException ignored) {}
		}
	}
	
	public static void log(String line) {
		DateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		System.out.println("["+date.format(new Date())+"] " + line);
	}
	
	public KingdomCharacter loadCharacter(String name) {
		File file = new File("characters", name.toLowerCase());
		if(!file.exists()) return null;
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(file));
			KingdomCharacter character = new KingdomCharacter();
			character.id = input.readInt();
			character.name = name;
			character.x = input.readInt();
			character.y = input.readInt();
			character.dx = input.readInt();
			character.dy = input.readInt();
			input.close();
			return character;			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(input != null) input.close();
			} catch (IOException ignored) {}
		}
	}

	public static class CharacterConnection extends Connection {
		public KingdomCharacter character;
		public Connection cHandle;
	}
	
	public static void main(String[] args) throws IOException {
		Log.set(Log.LEVEL_DEBUG);
		new KingdomServer();
		log("Server has been started");
	}
}
