package org.example.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/demo")
public class ServerEndpointDemo {

	// opens a connection
	@OnOpen
	public void handleOpen() {
		System.out.println("client is now connected...");
	}

	// exchanges messages with client
	@OnMessage
	public String handleMessage(String message) {
		System.out.println("receive from client: " + message);
		String replyMessage = "echo" + message;
		System.out.println("send to client: " + replyMessage);
		return replyMessage;
	}

	// closes the connection
	@OnClose
	public void handleClose() {
		System.out.println("client is now disconnected...");
	}

	// handles errors
	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace();
	}
}
