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


public class NewServer extends JFrame{

	//private JTextField userText;
	private JTextArea chatWindow;

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	private ServerSocket serverSocket;	//public server
	private Socket socket;

	ArrayList<ServerConnection> sCs = new ArrayList<ServerConnection>();
	ArrayList<InetAddress> ipAddress = new ArrayList<InetAddress>();
	ArrayList<String> memberList = new ArrayList<String>();
	int clientCount = 0;
	public int port;
	public int otherPort;
	private boolean setStreams = false;



	public NewServer(int port, int otherPort) {
		super("Server with port number: "+ port);

		chatWindow = new JTextArea();
		//chatWindow.setBackground(new Color(255,250,250));
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
	public void startRunning() throws EOFException {
		try {					

			serverSocket = new ServerSocket(port,100); //port number, backlog = how many people can wait to access this server
			connectToServer();
			doEverything();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//wait for connection and display connection information
	public void waitForConnection() throws IOException{

		//as it is in the called f el infinite loop .. it will keep checking kol shwaya for a connection ,, until connected
		showMessage("\nWaiting for someone to connect... \n"); //to show that the program is loaded
		Socket socket1 = serverSocket.accept(); // once having a connection .. a socket is created
		showMessage("\nConnected to client: " + socket1.getInetAddress().getHostName());
		ServerConnection sc = new ServerConnection(this, socket1);
		sc.start();
		sCs.add(sc);
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
				if(!setStreams) {
					serverSetsupConnection();
					setStreams = true;
				}
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
		outputStream = new ObjectOutputStream(socket.getOutputStream()); 
		outputStream.flush(); //to remove what is leftover from previous message

		inputStream = new ObjectInputStream(socket.getInputStream());

		showMessage("\nCreated streams with super\n");

	}

	//send message to super
	public void serverSendsMessage(String message) {
		try {
			if(outputStream!=null) {
				outputStream.writeObject("From port "+ port+": "+ message);
				outputStream.flush();
				showMessage("\nME: " + message);

			}

		}catch(IOException e) {
			chatWindow.append("\nERROR! message cannot be sent");
		}
	}

	//conversation with super
	public void whileUserChatting() throws IOException{
		System.out.println("?");
		String message = "";
		do {
			try {
				message = (String) inputStream.readObject();
				System.out.println(message + " Received from Super");
				if(message.contains("MEMLIST#")) {
					String[] memListArr = message.split("#");
					System.out.println(Arrays.toString(memListArr)+"  <====");
					int i = memberList.indexOf(memListArr[1]);
					if(i!=-1) {
						sCs.get(i).sendMessage(memListArr[2]);
					}
				}
				else if(message.split("#").length==4) {
					
					String[] arr = message.split("#");
					System.out.println(Arrays.toString(arr));
					int idx = memberList.indexOf(arr[1]);
					if(idx!=-1)
						sCs.get(idx).sendMessage(arr[0]+"#"+arr[2]);
				}
				showMessage("\n"+message);
			}catch(ClassNotFoundException e) {
				showMessage("\nUnknown object type");
			}

		}while(!message.equals("Server: BYE"));
	}

	public void doEverything() {
		Thread t1=new Thread() {
			public void run() {
				try {
					while(true) {
						waitForConnection();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		Thread t2=new Thread() {
			public void run() {
				try {
					whileUserChatting();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		t1.start();
		t2.start();
	}
}