package Server;

import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private String username;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			InputStream input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
			
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
			
			writer.println("Enter your username:");
			username = reader.readLine();
			Server.addUser(username, this);
			
			Server.broadcast(username + " has joined this chat", this);
			
			String clientMessage;
			while((clientMessage = reader.readLine()) != null) {
				handleMessage(clientMessage);
			}
		} catch(IOException e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			closeConnection();
		}
	}

	private void handleMessage(String message) {
		String[] tokens = message.split(" ", 3);
		String recipient = tokens[0];
		String dm = tokens[1];
		
		ClientHandler recipientHandler = Server.getUserHandler(recipient);
		if(recipientHandler != null) {
			recipientHandler.sendMessage(username + " (private) " + dm);
		} else {
			sendMessage("User " + recipient + " is not online..");
		}
	}

	public void sendMessage(String message) {
		writer.println(message);		
	}

	private void closeConnection() {
		try {
			socket.close();
			Server.removeUser(username);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}