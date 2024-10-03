package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import Server.Server;

public class Client {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private String username;
	private boolean isUsernameTaken = true;
	private MessageListener messageListener;
	
	public Client(String address, int port, String username, MessageListener listener) {
		try {
			this.socket = new Socket(address, port);
			this.username = username;
			this.messageListener = listener;
			
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
			
			InputStream input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
			
			writer.println(username);
			
			String usernameAvailable = reader.readLine();
			if("USERNAME_AVAILABLE".equals(usernameAvailable)) {
				System.out.println("username is still available");
				isUsernameTaken = false;
			} else if("USERNAME_TAKEN".equals(usernameAvailable)) {
				System.out.println("Username is already taken.");
				socket.close();
			}
			
			if(!isUsernameTaken) new Thread(this::listenForMessages).start();
			
			
		} catch(UnknownHostException e) {
			System.out.println("Sever not found: " + e.getMessage());
		} catch(IOException e) {
			System.out.println("I/O error: " + e.getMessage());
		}
	}
	
    public boolean getIsUsernameTaken() {
    	System.out.println(isUsernameTaken);
        return isUsernameTaken;
    }
	
	private void listenForMessages() {
		String message;
		try {
			while((message = reader.readLine()) != null) {
				System.out.println("received: " + message);
				messageListener.onMessageReceived(message);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message) {
		writer.println(message);
	}
}
