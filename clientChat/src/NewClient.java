import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class NewClient extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	//private ServerSocket serverSocket;	//public server
	private Socket socket;

	private String message = "";
	private String serverIP;

	private String username;
	private Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	int port;




	//constructor
	public NewClient(int port) { //passing in the ip address of the device
		super("Client");
		//serverIP = host;
		this.port = port;
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				userSendsMessage("BYE");
				e.getWindow().dispose();
			}
		});
		userText = new JTextField();
		userText.setEditable(false); // 3lshan prevent anything to be typed before connection with client 
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						userSendsMessage(event.getActionCommand()); //sending the string typed into that field
						userText.setText(""); // after sending the message it resets again
					}

				}
				);

		add(userText, BorderLayout.SOUTH);


		chatWindow = new JTextArea();
		//	chatWindow.setBackground(new Color(255,250,250));

		add(new JScrollPane(chatWindow));
		//add(chatWindow,BorderLayout.CENTER);
		chatWindow.setEditable(false);

		this.setLocation((int) ((dimension.getWidth() - this.getWidth()) / 2), (int) ((dimension.getHeight() - this.getHeight()) / 2));
		this.setSize(350,500);
		this.setVisible(true);

		serverIP = JOptionPane.showInputDialog("Please enter an IP address");


		//		while (usernameExists(username)) {
		//			username = JOptionPane.showInputDialog("username used");
		//		}
		//		//System.out.println(Arrays.toString(memberList.toArray()));
		//		

		//		join(username);

		userStartsRunning();


	}

	//	public boolean usernameExists(String name) {
	//		for(int i=0; i< memberList.size()-1; i++) {
	//			if(memberList.get(i) == username)
	//				return true;
	//		}
	//		return false;
	//	}


	//starting 
	public void userStartsRunning() {
		try {
			//			System.out.print("ewferg");
			connectToServer();	//server is waiting for connection
			userSetsupConnection();
			username = JOptionPane.showInputDialog("Please enter a username");
			userSendsMessage("joins#"+username);
			whileUserChatting();
		}
		catch(EOFException e) {
			usersShowMessage("\nServer terminatted the connection!");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {closeChatApp();}

	}

	//connect to server
	private void connectToServer() throws IOException{
		usersShowMessage("Attemping connection... \n");
		boolean flag = true;

		while(flag) {
			try {
				System.out.println("Hello");
				socket = new Socket(InetAddress.getByName(serverIP),port);
				chatWindow.selectAll();
				chatWindow.replaceSelection("\n");
				flag = false;
				//	userText.setEditable(true);


			} catch(ConnectException e) {
				//flag = false;
			}

		}
		usersShowMessage("Connected to server: " + socket.getInetAddress() );

	}

	// get stream to send and receive data
	public void userSetsupConnection() throws IOException{ //creating the connection to the computer the socket created
		outputStream = new ObjectOutputStream(socket.getOutputStream()); 
		outputStream.flush(); //to remove what is leftover from previous message

		inputStream = new ObjectInputStream(socket.getInputStream());

		usersShowMessage("\nCreated streams from client side\n");

	}

	//during conversation
	public void whileUserChatting() throws IOException{


		do {
			try {
				message = (String) inputStream.readObject();
				if(message.equals("ACK")) {
					userText.setEditable(true);
					this.setTitle(username);
				}
				else if(message.equals("ERR")){
					usersShowMessage("sorry, failed to joined");

					username = JOptionPane.showInputDialog("Username taken, please enter another username");
					userSendsMessage("joins#"+username);
				}
				else {
					usersShowMessage("\n"+message);
				}
			}catch(ClassNotFoundException e) {
				usersShowMessage("\nUnknown object type");
			}

		}while(!message.equals("Server: BYE"));
	}


	//close streams and sockets after done chatting
	private void closeChatApp() {
		usersShowMessage("\nClosing connections at client side\n");
		userAbleToType(false);
		try {
			outputStream.close();
			inputStream.close();
			socket.close();

		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	//send message to server
	public void userSendsMessage(String message) {
		try {
			System.out.println(message+"<-");
			if(!(message.contains("MEMLIST")||(message.contains("#")||(message.contains("BYE"))))) {
				JOptionPane.showMessageDialog(null, "Invalid message format", "InfoBox: " + "error", JOptionPane.INFORMATION_MESSAGE);

			}
			else {
				outputStream.writeObject("Client : " +message+"#2");
				outputStream.flush();
				usersShowMessage("\nME: " + message);
			}

		}catch(IOException e) {
			chatWindow.append("\nERROR! message cannot be sent");
		}
	}

	//updates chatWindow
	private void usersShowMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() { //created a thread
					public void run() {
						chatWindow.append(text);
					}
				}
				);
	}

	//give user permission to type in text box
	public void userAbleToType(final boolean b) {

		SwingUtilities.invokeLater(
				new Runnable() { //created a thread
					public void run() {
						userText.setEditable(b);
					}
				}
				);
	}



	public void chat(InetAddress source, InetAddress destination, int TTL, String message ) {

	}


}