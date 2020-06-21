import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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


public class SuperServer extends JFrame{

	//private JTextField userText;
	private JTextArea chatWindow;

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	private ServerSocket serverSocket;	//public server
	private Socket socket;

	ArrayList<SuperConnection> sCs = new ArrayList<SuperConnection>();
	ArrayList<InetAddress> ipAddress = new ArrayList<InetAddress>();
	ArrayList<String> serverList = new ArrayList<String>();
	ArrayList<String> allMemberList = new ArrayList<String>();
	int clientCount = 0;
	public int port;

	
	
	public SuperServer(int port) {
		super("Super with port number: "+ port);

		chatWindow = new JTextArea();
		//chatWindow.setBackground(new Color(255,250,250));
		serverList.add("Server 1");
		serverList.add("Server 1");
		add(new JScrollPane(chatWindow));
		//add(chatWindow,BorderLayout.CENTER);
		chatWindow.setEditable(false);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(450,500);
		this.setVisible(true);
		this.port = port;
	}

	//set up and run the server
	public void startRunning() {
		try {					

			serverSocket = new ServerSocket(port,100); //port number, backlog = how many people can wait to access this server
			while(true) {
				try {

					waitForConnection();
				}
				catch(EOFException e) {
					showMessage("\nOne of the servers ended the connection!");
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

		//as it is in the called f el infinite loop .. it will keep checking kol shwaya for a connection ,, until connected
		showMessage("\nWaiting for server to connect... \n"); //to show that the program is loaded
		Socket socket1 = serverSocket.accept(); // once having a connection .. a socket is created
		showMessage("\nConnected to server: " + socket1.getInetAddress().getHostName());
		SuperConnection sc = new SuperConnection(this, socket1);
		sc.start();
		sCs.add(sc);
	}
	

	//updates chatWindow
	public void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() { //created a thread
					public void run() {
							chatWindow.append("\n" +text);
							if(outputStream!=null) {
								try {
									outputStream.writeObject("Hello");
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
		return serverList;
	}
	
}