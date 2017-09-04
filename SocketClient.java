import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketClient {

	/**
	 * The client will:
		1)	Accept a machine name and port number to connect to as command line arguments.
		2)	Connect to the server.
		3)	Prompt for and send the user’s name.
		4)	Present the following menu of choices to the user:
			a.	Display the names of all known users.
			b.	Display the names of all currently connected users.
			c.	Send a text message to a particular user.
			d.	Send a text message to all currently connected users.
			e.	Send a text message to all known users.
			f.	Get my messages.
			g.	Exit.
		5)	Interact with the server to support the menu choices.
		6)	Ask the user for the next choice or exit.
	 */
	
	 Socket socket = null;
	 PrintWriter out = null;
	 BufferedReader in = null;
	 
	 static String newLine = System.getProperty("line.separator"); 
	 String menu = newLine +
			 "Select from the following menu:" + newLine +
			    		"		1.	Display the names of all known users." + newLine +
			    		"		2.	Display the names of all currently connected users." + newLine +
			    		"		3.	Send a text message to a particular user." + newLine +
			    		"		4.	Send a text message to all currently connected users." + newLine +
			    		"		5.	Send a text message to all known users." + newLine +
			    		"		6.	Get my messages." + newLine +
			    		"		7.	Exit." + newLine + newLine +
			    		"Enter your choice: ";
	 
	 public static void main(String[] args)
	 {
		 if (args.length != 2) {
			 System.out.println("Usage:  client hostname port");
	         System.exit(1);
	      }

	     SocketClient client = new SocketClient();

	     String host = args[0];
	     int port = Integer.valueOf(args[1]);
	     client.listenSocket(host, port);
	     client.communicate();
	     
	   }

	private void communicate() {
		Scanner sc = new Scanner(System.in);
	    System.out.println("Enter your name: ");
	    String name = sc.nextLine();

	    //Send data over socket
	    out.println(name);

	    //Receive text from server
	    try {
	    	String line = in.readLine();
	        System.out.println("Text received: " + line);
	    } 
	    catch (IOException e) {
	        System.out.println("Read failed");
	        System.exit(1);
	    }
	    String option = "";
	    while (!option.equals("7")) {
	    	System.out.print(menu);
	   
	    	option = sc.nextLine();
	    	// if option not in 1-7, error, else...
	    	
	    	if (!option.matches("[1-7]")) {
	    		System.out.println("  " + option + " is not a valid option" + newLine);
	    		continue;
	    	}
	    	//Send data over socket
		    out.println(option);
		  
	    	//Receive text from server
		    String line = "";
	    	try {
		    	line = in.readLine();
		        // System.out.println("Text received: " + line);
		    } 
		    catch (IOException e) {
		        System.out.println("Read failed");
		        System.exit(1);
		    }
	    	// process line according to option sent...
	    	switch (option) {
	    	case "1":		// 1.	Display the names of all known users.
	    		System.out.println("Known users:");
	    		String[] allUsers = line.split(",");
	    		int count = 0;
	    		for (String user: allUsers) {
	    			count++;
	    			System.out.println("   " + count + "   " + user);
	    		}
	    		break;
	    	case "2":
			    System.out.println("Currently connected users:");
	    		String[] connectedUsers = line.split(",");
	    		int count1 = 0;
	    		for (String user: connectedUsers) {
	    			count1++;
	    			System.out.println("   " + count1 + "   " + user);
	    		}
	    		break;
	    	case "3":
	    		System.out.println("3. Send a text message to a particular user.");
	    		break;
	    	case "4":
	    		System.out.println("4. send a text message to all currently connected users:");
	    		String message=sc.nextLine();
	    		out.println(message);
	    		break;
	    	case "5":
	    		System.out.println("5. Send a text message to all known users.");
	    		
	    		break;
	    	case "6":
	    		System.out.println("6. Get my messages.");
	    		break;
	    	case "7":
	    		System.out.println("7. Exit.");
	    		break;
	    	default:
	    		System.out.println(newLine + "unable to process option " + option + newLine);
	    	}
	    	
	    	
	    }
	    System.out.println("Client exiting ...");
	}

	private void listenSocket(String host, int port) {
		//Create socket connection
	      try {
	    	  socket = new Socket(host, port);
	    	  out = new PrintWriter(socket.getOutputStream(), true);
	    	  in = new BufferedReader
	    		   (new InputStreamReader(socket.getInputStream()));
	      } 
	      catch (UnknownHostException e) {
	    	  System.out.println("Unknown host");
	    	  System.exit(1);
	      } 
	      catch (IOException e) {
	    	  System.out.println(e.getMessage());
	    	  System.out.println("No I/O");
	    	  System.exit(1);
	      }
	}
}