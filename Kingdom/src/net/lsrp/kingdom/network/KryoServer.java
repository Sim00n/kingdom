package net.lsrp.kingdom.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import net.lsrp.kingdom.network.Network.AddCharacter;
import net.lsrp.kingdom.network.Network.ChatMessage;
import net.lsrp.kingdom.network.Network.ConnectionEstablished;
import net.lsrp.kingdom.network.Network.Login;
import net.lsrp.kingdom.network.Network.MoveCharacter;
import net.lsrp.kingdom.network.Network.Register;
import net.lsrp.kingdom.network.Network.RegistrationRequired;
import net.lsrp.kingdom.network.Network.UpdateCharacter;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class KryoServer {

	Server server;
	HashSet<KryoCharacter> loggedIn = new HashSet<KryoCharacter>();
	
	public static final int PORT = 54555;
	
	public KryoServer() throws IOException {
		server = new Server() {
			protected Connection newConnection() {
				return new CharacterConnection();
			}
		};
		
		Network.register(server);
		
		server.addListener(new Listener() {
			@Override
			public void received(Connection c, Object object) {
				CharacterConnection connection = (CharacterConnection)c;
				KryoCharacter character = connection.character;
				
				if(object instanceof Login) {
					if(character != null) return;
				
					String name = ((Login)object).name;
					if(!isValid(name)) {
						c.close();
						return;
					}
					
					for(KryoCharacter other : loggedIn) {
						if(other.name.equals(name)) {
							disconnectCharacter(name);
							c.close();
							return;
						}
					}
					
					character = loadCharacter(name);
					
					if(character == null) {
						c.sendUDP(new RegistrationRequired());
						return;
					}
					
					loggedIn(connection, character);
					ConnectionEstablished conne = new ConnectionEstablished();
					conne.motd = "Testowy string motd";
					conne.id = character.id;
					c.sendUDP(conne);
							
					return;
				}
				
				
				if(object instanceof Register) {
					if(character != null) return;
					
					Register register = (Register)object;
					
					if(!isValid(register.name)) {
						c.close();
						return;
					}
					
					if(loadCharacter(register.name) != null) {
						c.close();
						return;
					}
					
					character = new KryoCharacter();
					character.name = register.name;
					character.x = 100;
					character.y = 100;
					character.dx = 0;
					character.dy = 0;
					if(!saveCharacter(character)) {
						c.close();
						return;
					}
					
					loggedIn(connection, character);
					return;
				}
				
				
				if(object instanceof MoveCharacter) {
					if(character == null) return;
					
					MoveCharacter msg = (MoveCharacter)object;
					character.x = msg.x;
					character.y = msg.y;
					character.dx = msg.dx;
					character.dy = msg.dy;
					if(!saveCharacter(character)) {
						connection.close();
						return;
					}
					
					UpdateCharacter update = new UpdateCharacter();
					update.id = character.id;
					update.x = character.x;
					update.y = character.y;
					update.dx = character.dx;
					update.dy = character.dy;
					server.sendToAllUDP(update);
					return;
				}
				
				if(object instanceof ChatMessage) {
					//if(character != null) return;
					
					ChatMessage chat = (ChatMessage)object;
					server.sendToAllTCP(chat);
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
				
			}
		});
		
		//server.bind(Network.port, Network.port);
		server.bind(KryoServer.PORT, KryoServer.PORT);
		server.start();
	}
	
	void loggedIn(CharacterConnection c, KryoCharacter character) {
		c.character = character;
		
		for(KryoCharacter other : loggedIn) {
			AddCharacter addCharacter = new AddCharacter();
			addCharacter.character = other;
			c.sendUDP(addCharacter);
		}
		
		loggedIn.add(character);
		
		AddCharacter addCharacter = new AddCharacter();
		addCharacter.character = character;
		server.sendToAllUDP(addCharacter);
	}
	
	void disconnectCharacter(String name) {
		for(KryoCharacter other : loggedIn) {
			if(other.name.equals(name)) {
				loggedIn.remove(other);
			}
		}
	}
	
	boolean saveCharacter(KryoCharacter character) {
		File file = new File("characters", character.name.toLowerCase());
		file.getParentFile().mkdirs();
		
		if(character.id == 0) {
			String[] children = file.getParentFile().list();
			if(children == null) return false;
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
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				output.close();
			} catch (IOException ignored) {}
		}
	}
	
	KryoCharacter loadCharacter(String name) {
		File file = new File("characters", name.toLowerCase());
		if(!file.exists()) return null;
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(file));
			KryoCharacter character = new KryoCharacter();
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
		public KryoCharacter character;
	}
	
	public static void main(String[] args) throws IOException {
		Log.set(Log.LEVEL_DEBUG);
		new KryoServer();
		System.out.println("Main wystartował");
	}
}
