package net.lsrp.kingdom.input;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.lsrp.kingdom.Game;
import net.lsrp.kingdom.network.KryoClient;
import net.lsrp.kingdom.network.Network.ChatMessage;

public class Chat {

	private static String myMessage = "";
	public static List<ChatMessage> chat = new ArrayList<ChatMessage>();
	private static int tlc = Game.getWindowHeight() - (Game.getWindowHeight() / 3) - 40; //Top left corner - long calculation ...
	private static int cursorTime = 0; 
	private static final int CHAT_SIZE = 12;
	
	private static boolean typing = false;
	private static boolean timestamp = false;
	
	public static void type() {
		typing = true;
	}
	
	public static void endTyping() {
		typing = false;
	}
	
	public static boolean isTyping() {
		return typing;
	}
	
	public static void update() {
		if(isTyping())
			cursorTime++;
	}
	
	public static void render(Graphics g) {
		g.setColor(new Color(0, 0, 0, 70));
		g.fillRect(10, tlc, Game.getWindowWidth() / 2 + (Game.getWindowWidth() / 4) , Game.getWindowHeight() / 3 + 10);
		g.setColor(new Color(255, 255, 255));
		g.setFont(new Font("Tahoma", Font.BOLD, 11));
		for(int i = 0; i < chat.size(); i++) {
			if(i > chat.size() - CHAT_SIZE) {
				if(chat.get(i) != null) {
					if(chat.get(i).message != "") {
						DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss] ");
						String _cAuthor = chat.get(i).authorName + " (" + chat.get(i).id + "): ";
						String _cTime = dateFormat.format(new Date(chat.get(i).timestamp));
						String _cMessage = chat.get(i).message; 
						String _cOutput = "";
						
						if(!timestamp)
							_cOutput += _cTime;
						if(chat.get(i).id != -1)
							_cOutput += _cAuthor;
						_cOutput += _cMessage;
						
						g.drawString(_cOutput, 20, Game.getWindowHeight() - 37 + 15 + (15 * (i - chat.size())));
					}
				}
			}
		}
		
		if(isTyping()) {
			g.setColor(new Color(0, 0, 0, 100));
			g.fillRect(10, Game.getWindowHeight() - 25, Game.getWindowWidth() / 2 + (Game.getWindowWidth() / 4), 20);
			g.setColor(new Color(255, 255, 255, 255));
			if(myMessage != null)
				if(cursorTime < 30)
					g.drawString(myMessage + "|", 20, Game.getWindowHeight() - 10);
				else {
					g.drawString(myMessage, 20, Game.getWindowHeight() - 10);
					if(cursorTime > 60)
						cursorTime = 0;
				}
		}
	}
	
	public static void addChar(char c) {
		if(isTyping())
			if(getChatLength() < 100) 
				myMessage += c;
	}
	
	public static void deleteChar() {
		if(getChatLength() > 0)
			myMessage = myMessage.substring(0, myMessage.length() - 1);
	}
	
	public static void send() {
		
		if(myMessage.length() > 0) {
			if(myMessage.charAt(0) == '/') {
				String cmd = myMessage.substring(1);
				
				if(cmd.equals("q")) {
					System.exit(0);
				} else if(cmd.equals("timestamp")) {
					timestamp = !timestamp;
				} else if(cmd.equals("help")) {
					ChatMessage chatmsg = new ChatMessage();
					chatmsg.id = -1;
					chatmsg.timestamp = System.currentTimeMillis();
					chatmsg.message = "Commands: /q, /timestamp, /help";
					chatmsg.authorName = "Info";
					chat.add(chatmsg);
				}
				
				myMessage = "";
				endTyping();
			} else {
				myMessage = myMessage.substring(0, 1).toUpperCase() + myMessage.substring(1);
				char lastChar = myMessage.charAt(myMessage.length() - 1);
				if(lastChar != '.' && lastChar != '?' && lastChar != '!') {
					myMessage += '.';
				}
				
				ChatMessage chatmsg = new ChatMessage();
				chatmsg.id = Game.id;
				chatmsg.timestamp = System.currentTimeMillis();
				chatmsg.message = myMessage;
				chatmsg.authorName = Game.username;
				
				KryoClient.chatmsg = chatmsg;
				myMessage = "";
				endTyping();
			}
		} else {
			endTyping();
		}
	}
	
	public static void addToChat(ChatMessage msg) {
		chat.add(msg);
	}
	
	public static int getChatLength() {
		if(myMessage != null) 
			return myMessage.length();
		else
			return 0;
	}
}
