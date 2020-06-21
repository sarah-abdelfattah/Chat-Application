import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class ServerConnection extends Thread {
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private NewServer server;
	public String myUsername = null;
	private int myIndex;




	public ServerConnection(NewServer server, Socket socket) {
		super("ServerConnectionThread");
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
				
				System.out.println(message);

				if(myUsername == null){	
					System.out.println(myUsername);
					String joinUserName = message.split("#")[1];
					System.out.println();
					joins(joinUserName);
				}
				else if(message.contains("MEMLIST")) {
					server.serverSendsMessage("MEMLIST"+"#"+myUsername);
					System.out.println("MEMLIST"+"#"+myUsername);
				}
				else {
					String[] sentToUsername = message.split("#");
					System.out.println(Arrays.toString(sentToUsername));
					//					if(this==server.sCs.get(0)) {
					//						
					//					}
					//					else 
					if(sentToUsername.length == 3) {						
						ServerConnection c = null;
						boolean found = false;
						for(int i =0;i < server.memberList.size(); i++) {
							if(server.memberList.get(i).equals(sentToUsername[0].substring(9, sentToUsername[0].length()))) {
								found = true;
								c = server.sCs.get(i);
								myIndex = i;
								System.out.println(server.memberList.size()+" memList length");
								System.out.println(server.ipAddress.size()+" ipSize length");
								System.out.println(server.sCs.size()+" sCs length");
								break;
							}
						}

						for(int i = 0;i<server.sCs.size();i++) {


							if(server.sCs.get(i) == c) {
								server.sCs.get(i).sendMessage(myUsername+"#" + sentToUsername[1]);
							}
							server.showMessage(myUsername+" " + ": " + sentToUsername[1]+"#"+(Integer.parseInt(sentToUsername[2])-1));

						}
						System.out.println(found + "found");
						System.out.println(sentToUsername[2]);
						if(!found&&(Integer.parseInt(sentToUsername[2])-1)!=0) {
							System.out.println(myUsername+"#"+ sentToUsername[0].substring(9, sentToUsername[0].length())+"#"+sentToUsername[1]+"#"+(Integer.parseInt(sentToUsername[2])-1) + "anaaa f el server connection");
							server.serverSendsMessage(myUsername+"#"+ sentToUsername[0].substring(9, sentToUsername[0].length())+"#"+sentToUsername[1]+"#"+(Integer.parseInt(sentToUsername[2])-1));
						}
					}
					else if(!message.contains("Client : BYE")){
						//MSH HNAAAAA
						System.out.println("MSH 7ND5OL HeNAAAAAA");
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}while(!message.contains("Client : BYE"));
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
			System.out.println("terminating");
			server.serverSendsMessage("REMLIST#"+myUsername);
			outputStream.close();
			inputStream.close();
			socket.close();
			server.sCs.remove(this);
			server.ipAddress.remove(socket.getInetAddress());
			server.memberList.remove(server.memberList.indexOf(myUsername));

		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void joins(String joinUsername) {
		if(!server.memberList.contains(joinUsername)) {
			myUsername = joinUsername;
			server.memberList.add(joinUsername);
			server.serverSendsMessage("JOIN#"+joinUsername);
			sendMessage("ACK");
			System.out.println("ACK");
		}
		else {
			sendMessage("ERR");
			System.out.println("ERR");

		}
	}
}