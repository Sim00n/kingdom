package net.lsrp.kingdom.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	
	//public static final int port = 54555;

	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Login.class);
		kryo.register(ConnectionEstablished.class);
		kryo.register(RegistrationRequired.class);
		kryo.register(Register.class);
		kryo.register(AddCharacter.class);
		kryo.register(UpdateCharacter.class);
		kryo.register(RemoveCharacter.class);
		kryo.register(KryoCharacter.class);
		kryo.register(MoveCharacter.class);
		kryo.register(ChatMessage.class);
	}
	
	public static class Login {
		public String name;
		public int id;
	}
	
	public static class ConnectionEstablished {
		public String motd;
		public int id;
	}
	
	public static class RegistrationRequired {
		
	}
	
	public static class Register {
		public String name;
		public int x, y;
	}
	
	public static class UpdateCharacter {
		public int id, x, y, dx, dy;
	}
	
	public static class AddCharacter {
		public KryoCharacter character;
	}
	
	public static class RemoveCharacter {
		public int id;
	}
	
	public static class MoveCharacter {
		public int id, x, y, dx, dy;
	}
	
	public static class ChatMessage {
		public int id;
		public long timestamp;
		public String message;
		public String authorName;
	}

}