//  John Paschal  G00861791
//  Songyue Huang G00864305


import java.io.*;
import java.net.*;
import java.util.*;

public class AuctionServer {

public static final int SELLER_PORT = 7999;
public static final int BUYER_PORT = 8001;
// AUCTIONSERVER_IP_ADDRESS is in Buyer/Seller Thread class.
public static final int SELLER = 0;
public static final int BUYER = 1;
public static int buyer_num = 0;


public static ArrayList<Record> ds = new ArrayList<Record>();
public static ArrayList<Socket> sockets = new ArrayList<Socket>();

	public static void main(String[] args) {


		try {

			ServerSocket buyerSocket = null;
			buyerSocket = new ServerSocket(BUYER_PORT);
	
			System.out.println("Server Initialized\n");
			System.out.println("Waiting for clients\n");
	
			//start a new Thread to wait for the seller, when seller connects, thread stops
			SellerFinderThread seller_thread = new SellerFinderThread();
			seller_thread.start();
	
			while (true) {

				Socket buyer_client = buyerSocket.accept();	
				sockets.add(buyer_client);
				System.out.println("Buyer " + ++buyer_num + " connected!\n");


				ServerThread buyer_thread = new ServerThread(buyer_client, buyer_num);	

				buyer_thread.start();
			}

		}  catch (IOException e) {}

	} // end of main

}// end of class
