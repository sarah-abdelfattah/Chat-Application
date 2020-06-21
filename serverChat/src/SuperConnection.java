import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class SuperConnection extends Thread {
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private SuperServer server;
	private int myIndex;




	public SuperConnection(SuperServer server, Socket socket) {
		super("SuperConnectionThread");
		this.socket = socket;
		this.server = server;

		server.ipAddress.add(socket.getInetAddress());
	}

	@Override
	public void run() {
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream()); 
			outputStream.flush();
			inputStream = new ObjectInputStream(socket.getInputStream());
			server.showMessage("\nCreated streams from server side\n");
			try {
				whileChatting();
			}catch(SocketException e) {

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //to remove what is leftover from previous message
	}

	public void whileChatting() throws IOException{
		String message = "You are now connected! ";
		sendMessage(message);

		do {

			try {
				message = (String) inputStream.readObject();	//save messages came from client
				System.out.println(message+" 011");
				server.showMessage(message);
				if(message.contains("JOIN#")) {
					server.allMemberList.add(message.substring(21));
				}
				else if(message.contains("MEMLIST")) {
					for(int i =0; i<server.sCs.size(); i++) {
						//System.out.println(message.substring(16));
						server.sCs.get(i).sendMessage(message.substring(16)+"#"+Arrays.toString(server.allMemberList.toArray()));
					}
				}
				else if(message.contains("REMLIST")) {
					String name = message.split("#")[1];
					server.allMemberList.remove(server.allMemberList.indexOf(name));
				}
				else if(message.split("#").length==4 && server.sCs.size()==2) {
					if(this!=server.sCs.get(0)) {
						System.out.println("ana f el super connectionnnn");
						server.sCs.get(0).sendMessage(message.substring(16));
					}
					else {
						server.sCs.get(1).sendMessage(message.substring(16));
					}
				}	
			}
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}while(!message.equals("Client : BYE"));
		terminateClient();
	}

	public void sendMessage(String message) {
		try {
			outputStream.writeObject(message);
			outputStream.flush();

		}catch(IOException e) {
			server.showMessage("\nERROR! message cannot be sent");
		}
	}

	public void terminateClient() {
		try {
			outputStream.close();
			inputStream.close();
			socket.close();
			server.sCs.remove(this);
			server.ipAddress.remove(socket.getInetAddress());
			server.serverList.remove(server.sCs.indexOf(this));

		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}