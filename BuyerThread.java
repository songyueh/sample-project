// John Paschal G00861791
// Songyue Huang G00864305


import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.Scanner;


public class BuyerThread extends Thread {

public static InetAddress AUCTIONSERVER_IP_ADDRESS;

public static int buyer_count = 0;
private static int buyer_number;

	public BuyerThread() {
		buyer_number = ++buyer_count;
	}

	public int getBuyerID() {
		return this.buyer_number;
	}

	public void run() {

	try {
		AUCTIONSERVER_IP_ADDRESS = InetAddress.getByName("localhost");
	} catch (UnknownHostException uhe) {}

	try {

		InetAddress server_addr = AUCTIONSERVER_IP_ADDRESS; 
		Socket client = new Socket(server_addr, AuctionServer.BUYER_PORT);
		

		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		PrintWriter output = new PrintWriter(client.getOutputStream(), true);

		String message;
		// print hello to buyer
		System.out.println(in.readLine());	
		System.out.println();

		// receive input from user
		Scanner kb = new Scanner(System.in);
		System.out.print("Type Command: ");
		message = kb.nextLine();
		
		// send message to server
		output.println(message);	
	
		// get return msg from server	
		String msg = in.readLine();
		while (!(msg.equals("finished"))) {
			System.out.println(msg + "\n");
			msg = in.readLine();
		}

		System.out.print("Type Command: ");
		while ((message = kb.nextLine()) != null) {
			output.println(message);
	
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

	} catch (IOException e) {}

	}// end of run

	public static void main (String[] args) {
		
		BuyerThread buyer = new BuyerThread();
		buyer.start();

	}
	
}	
