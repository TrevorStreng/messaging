package application;
	
import java.util.Set;

import Client.Client;
import Client.MessageListener;
import Server.Server;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class Main extends Application implements MessageListener {
	private Client client;
	private TextArea messageDisplay;
	
	@Override
	public void start(Stage primaryStage) {
		try {
	        // Create UI components
	        messageDisplay = new TextArea();
	        messageDisplay.setEditable(false);
	        TextField messageInput = new TextField();
	        Button sendButton = new Button("Send");

	        // Layout
	        VBox layout = new VBox(10, messageDisplay, messageInput, sendButton);
	        Scene scene = new Scene(layout, 800, 700);

	        // Setup Stage
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Private Messaging App");
	        primaryStage.show();
	        
	        
	        usernameInputWindow(new Stage());
	        
	        // Event handling for sending messages
	        sendButton.setOnAction(e -> {
	        String message = messageInput.getText();
	        messageDisplay.appendText("You: " + message + "\n");
	        client.sendMessage(message);
	        messageInput.clear();
	        });
	        } catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void usernameInputWindow(Stage parentStage) {
	    TextField usernameInput = new TextField();
	    Button submitButton = new Button("Submit");
	    
	    VBox usernameLayout = new VBox(6, usernameInput, submitButton);
	    Scene usernameScene = new Scene(usernameLayout, 200, 200);
	    
	    submitButton.setOnAction(e -> {
	    	String username = usernameInput.getText();
	    	System.out.println(username);
	    	client = new Client("localhost", 1234, username, this);
	    	
	    	// check if username is in use and display an error message if it is
	    	if(client.getIsUsernameTaken()) {
	    		Stage unavailable = new Stage();
	    		Text error = new Text(50, 50, "Username unavailable..");
	    		Button okayButton = new Button("Okay");
	    		
	    		VBox errorLayout = new VBox(6, error, okayButton);
	    		Scene errorScene = new Scene(errorLayout, 50, 50);
	    		
	    		okayButton.setOnAction(l -> {
	    			unavailable.close();
	    		});
	    		
	    		unavailable.setScene(errorScene);
	    		unavailable.show();
	    	} else {
	    		parentStage.close();
	    	}
	    });
	    
	    parentStage.setScene(usernameScene);
	    parentStage.show();
	}
	
    @Override
    public void onMessageReceived(String message) {
        // Update the UI with the new message (on the JavaFX Application Thread)
        javafx.application.Platform.runLater(() -> {
            messageDisplay.appendText("Dm: " + message + "\n");
        });
    }
	
	public static void main(String[] args) {
		launch(args);		
	}
}
