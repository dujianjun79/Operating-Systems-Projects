import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 * The server will:
 * 
	1)	Accept a port number as a command line argument.
	2)	Accept connections from clients.
	3)	Create a new thread for each client.
	4)	Store messages sent to each user.
	5)	End by termination with control-C.
 * 
 */
class SocketServer 
{
   ServerSocket server = null;
   // Hash map contains all users who have been connected and head node
   // for linked list of messages
   public static HashMap<String, MessageNode> users = new HashMap<String, MessageNode>();
   // ArrayList disconnected contains all users who have disconnected
   public static ArrayList<String> disconnected = new ArrayList<String>();
   // Semaphore to protect update of users and disconnected
   public static Semaphore mutex = new Semaphore(1,true);
   
   public void listenSocket(int port) {
      try {
    	  server = new ServerSocket(port); 
    	  System.out.println("Server running on port " + port + 
	                     "," + " use ctrl-C to end");
      } 
      catch (IOException e) {
    	  System.out.println("Error creating socket");
    	  System.exit(-1);
      }
      while(true)
      {
         ClientWorker w;
         try {
            w = new ClientWorker(server.accept());
            Thread t = new Thread(w);
            t.start();
         } 
         catch (IOException e) {
        	 System.out.println("Accept failed");
        	 System.exit(-1);
         }
      }
   }

   protected void finalize()
   {
      try
      {
         server.close();
      } 
      catch (IOException e) 
      {
         System.out.println("Could not close socket");
         System.exit(-1);
      }
   }

   public static void main(String[] args)
   {
      if (args.length != 1)
      {
         System.out.println("Usage: java SocketThrdServer port");
	 System.exit(1);
      }

      SocketServer server = new SocketServer();
      int port = Integer.valueOf(args[0]);
      server.listenSocket(port);
   }
}