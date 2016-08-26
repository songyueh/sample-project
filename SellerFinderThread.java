// John Paschal G00861791
// Songyue Huang G00864305

import java.io.*;
import java.net.*;
import java.lang.Thread;

public class SellerFinderThread extends Thread {


	public void run() {
	
	try {	
		ServerSocket sellerSocket = null;
		sellerSocket = new ServerSocket(AuctionServer.SELLER_PORT);

		while (true) {
			// accept seller client			
			Socket seller_client = sellerSocket.accept();

			// create a new thread to handle this client (Seller)	
			ServerThread seller_thread = new ServerThread(seller_client, AuctionServer.SELLER);
			seller_thread.start();
			System.out.println("Starting seller thread");
		}
	} catch (IOException e) {}	

	}


}	
