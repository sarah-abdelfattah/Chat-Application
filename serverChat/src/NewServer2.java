import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import javax.swing.*;


public class NewServer2 extends JFrame{

	//private JTextField userText;
	private JTextArea chatWindow;

	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private ServerSocket serverSocket;	//public server
	private Socket socket;

	ArrayList<ServerConnection> sCs = new ArrayList<ServerConnection>();
	ArrayList<InetAddress> ipAddress = new ArrayList<InetAddress>();
	ArrayList<String> memberList = new ArrayList<String>();
	int clientCount = 0;
	public int port;
	public int otherPort;



	public NewServer2(int port, int otherPort) {
		super("Server with port number: "+ port);

		chatWindow = new JTextArea();
		//chatWindow.setBackground(new Color(255,250,250));
		memberList.add("OtherServer");
		add(new JScrollPane(chatWindow));
		//add(chatWindow,BorderLayout.CENTER);
		chatWindow.setEditable(false);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(450,500);
		this.setVisible(true);
		this.port = port;
		this.otherPort = otherPort;
	}

	//set up and run the server
	public void startRunning() {
		try {					

			serverSocket = new ServerSocket(port,100); //port number, backlog = how many people can wait to access this server
			connectToServer();
			while(true) {
				try {

					waitForConnection();
				}
				catch(EOFException e) {
					showMessage("\nClient ended the connection!");
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//wait for connection and display connection information
	public void waitForConnection() throws IOException{

		if(sCs.size()==0) {
			serverSetsupConnection();
			whileUserChatting();
		}
		//as it is in the called f el infinite loop .. it will keep checking kol shwaya for a connection ,, until connected
		showMessage("\nWaiting for someone to connect... \n"); //to show that the program is loaded
		Socket socket1 = serverSocket.accept(); // once having a connection .. a socket is created
		showMessage("\nConnected to client: " + socket1.getInetAddress().getHostName());

	}

	public void connectToServer() throws IOException{
		showMessage("Attemping connection to another server ... \n");
		boolean flag = true;

		while(flag) {
			try {
				socket = new Socket(InetAddress.getByName("localhost"),otherPort);
				chatWindow.selectAll();
				chatWindow.replaceSelection("\n");
				flag = false;
				//	userText.setEditable(true);


			} catch(ConnectException e) {
				//flag = false;
			}

		}
		showMessage("Connected to another server: " + socket.getInetAddress() );

	}

	//updates chatWindow
	public void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() { //created a thread
					public void run() {
						chatWindow.append("\n" +text);
						if(outputStream!=null) {
							try {
								outputStream.writeUTF("Hello");
								outputStream.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				);
	}

	public int getIpcount(InetAddress inetAddress) {
		for(int j =0; j<ipAddress.size(); j++) 
			if(ipAddress.get(j)== inetAddress)
				return (j+1);
		return -1;
	}	


	public ArrayList<String> getMemberList(){
		return memberList;
	}

	public void serverSetsupConnection() throws IOException{ //creating the connection to the computer the socket created
		outputStream = new DataOutputStream(socket.getOutputStream()); 
		//		outputStream.flush(); //to remove what is leftover from previous message
		inputStream = new DataInputStream(socket.getInputStream());

		showMessage("\nCreated streams from other server side\n");
		System.out.println("here");
		sendsMessage("Hi");
	}


	//server sends message to another server
	public void sendsMessage(String message) {
		try {
			outputStream.writeUTF("Other server : " +message);
			outputStream.flush();
			showMessage("\nME: " + message);

		}catch(IOException e) {
			chatWindow.append("\nERROR! message cannot be sent");
		}
	}

	public void whileUserChatting() throws IOException{
		String message = "";
		System.out.println("brdo msh hello :P");
		int i = 0;
		do {
			if(i==0) {
				System.out.println("brdo msh hello :P");
				sendsMessage("NOTHello");
			}
			i++;
			message = (String) inputStream.readUTF();
			showMessage("\n"+message);

		}while(!message.equals("Server: BYE"));
	}

}