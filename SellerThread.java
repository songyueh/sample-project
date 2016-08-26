// John Paschal G00861791
// Songyue Huang G00864305


import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.*;
import java.util.concurrent.*;

public class SellerThread extends Thread {

public static InetAddress AUCTIONSERVER_IP_ADDRESS;

//public static final int SELLER_PORT = 7999;

	public void run(){

	try {
		AUCTIONSERVER_IP_ADDRESS = InetAddress.getByName("localhost");
	} catch (UnknownHostException uhe) {}	

	try {
		InetAddress server_addr = AUCTIONSERVER_IP_ADDRESS; 
		Socket client = new Socket(server_addr, AuctionServer.SELLER_PORT);
		
		System.out.println("Seller Connected to Server\n");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		PrintWriter output = new PrintWriter(client.getOutputStream(),true);	

		String message;

		System.out.println(in.readLine());
		System.out.println();

		//receive input from user
		Scanner kb = new Scanner(System.in);
		System.out.print("Type Command: ");
		message = kb.nextLine();

		// send command to server
		output.println(message);


		String msg = in.readLine();
		while (!(msg.equals("finished"))) { 
			System.out.println(msg + "\n");
			msg = in.readLine();
	        }				
			
		System.out.print("Type Command: ");
		while ((message = kb.nextLine()) != null) {
			output.println(message);

			//AuctionServer.wait_for_server_msg.acquire();
			String msg1 = in.readLine();
			while (!(msg1.equals("finished"))) { 
				System.out.println(msg1 + "\n");
				msg1 = in.readLine();
		        }				
				
			System.out.print("Type Command: ");
		}	







		while (true) {
	
			message = in.readLine();
			if (message == null) {
				System.out.println("breaking");
				break;
			}
			System.out.println(message + "\n");
		}

	} catch (IOException e) {
		e.printStackTrace();
	}
	//} catch (InterruptedException ie) {}
	}

public static void main(String[] args) {
	SellerThread seller = new SellerThread();
	seller.start();
}

}

