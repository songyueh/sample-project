// John Paschal G00861791
// Songyue Huang G00864305

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.Scanner;
import java.lang.StringBuilder;

// this class is a thread that starts running when a client (buyer or seller) 
// connects to the AuctionServer. Each thread deals with its individual
// client to achieve concurrency


public class ServerThread extends Thread {

	Socket client; // newly connected cient socket
	int id; // 0 for seller, 1 for buyer
	String buyer_name;
	
	BufferedReader in;
	PrintWriter out;



	public ServerThread(Socket client, int id) {
		this.client = client;
		this.id = id;		
		buyer_name = "";
	}	

	public void run() {


	try {
		// set up input and output stream for new client 
		 in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		 out = new PrintWriter(client.getOutputStream(), true);

	} catch(IOException e) {
		e.printStackTrace();
	}

		if (id == 0) {  
			out.println("Hello to Seller");		
			dealWithSeller();
			//out.println("Hello to Seller");

		} else { 
			out.println("Hello to Buyer " + id );		
			dealWithBuyer();
		}

	} // end of run

	public void dealWithSeller() {

		while (true) {
			String message = "";
			try {
				message = in.readLine();
			} catch (IOException e) {}
			if (message == null) {
				try {
					this.client.close();
				} catch (IOException io) {}
				break;		
			}

			
			//decompose message
			String tokens[] = message.split(" ");

			String cmd = tokens[0];
			if (cmd.equalsIgnoreCase("login")) {
				System.out.println("Seller has logged in\n");
				out.println("You are logged in as Seller");	
				out.println("finished");
			}else if (cmd.equalsIgnoreCase("list")) {
				System.out.println("Seller sends the command list\n");
				if (AuctionServer.ds.size() == 0) {
					out.println("There are no items to list");
				}
				for (int i = 0; i < AuctionServer.ds.size(); i++) {
					Record current = AuctionServer.ds.get(i);	
					out.println();
					out.println("Item: " + current.ID);
					out.println("Item name: " + current.name);
					out.println("Highest Bid: " + current.highest_bid);
					out.println("Highest Bidder: " + current.bidder_name);
					out.println();
				}
				out.println("finished");							


			}else if (cmd.equalsIgnoreCase("add")) {
				String result = tokens[2];
				int count = 0;	
				int duplicate = 0;
				while (count < (tokens.length -3)) {
					count++;
					result = result.concat(" ");
					result = result.concat(tokens[count + 2]);
				}

				int item_number = Integer.parseInt(tokens[1]);
				if (AuctionServer.ds.size() == 0) {
					Record temp = new Record(item_number,result);
					AuctionServer.ds.add(temp);
					System.out.println("Seller added Item " + item_number + " to the list\n");
					out.println("You have added item number " + item_number + " to the list");
					out.println("finished");
				}else {
					int temp_size = AuctionServer.ds.size();
					for (int i = 0; (i < temp_size) && (duplicate != 1); i++) {
						Record current = AuctionServer.ds.get(i);	
						if (current.ID == item_number) {
							System.out.println("Seller entered duplicate item number\n");
							out.println("You entered a duplicate item number");
							out.println("finished");
							duplicate = 1;
							//break;
						}
					}	
						if (duplicate == 0) {
						Record temp = new Record(item_number,result);
						AuctionServer.ds.add(temp);
						System.out.println("Seller added Item " + item_number + " to the list\n");
						out.println("You have added item number " + item_number + " to the list");
						out.println("finished");
						}					
				} //end of else	
			}else if (cmd.equalsIgnoreCase("sell")) {
				//System.out.println("selling item");
				PrintWriter output = null;
				Record to_sell = null;
				int to_sell_index = -1;
				Socket temp_socket;
				int item_num = Integer.parseInt(tokens[1]);
				for (int i = 0; i < AuctionServer.ds.size(); i++) {
					Record current = AuctionServer.ds.get(i);
					if (current.ID == item_num) {
						to_sell = current;
						to_sell_index = i;
					}
				}
				if (to_sell == null) {
					System.out.println("Seller tried to sell an unknown item\n");
					out.println("Cannot sell an item that does not exist");
					out.println("finished");
				}else {
					//check to see if buyer is still connected
					if (to_sell.bidder_name.equals("no bids")) {
						//just remove the item
						AuctionServer.ds.remove(to_sell_index);
						System.out.println("Item " + item_num + " has no bids, it has been removed from the list\n");
						out.println("Item " + item_num + "  has no bids, it has been removed from the list");
						out.println("finished");
					}else if (to_sell.bidder_left == 1) {
						System.out.println("Item number " + item_num + " has been sold to " + to_sell.bidder_name + "\n");
						out.println("Item number " + item_num + " sold successfully to " + to_sell.bidder_name + "\n");
						out.println("finished");	
						AuctionServer.ds.remove(to_sell_index); // remove item
					} else {

						temp_socket = to_sell.highest_bidder;
						for (int i = 0; i < AuctionServer.sockets.size(); i++) {
							if (AuctionServer.sockets.get(i) == temp_socket) {
								try {
									output = new PrintWriter(AuctionServer.sockets.get(i).getOutputStream(), true);
								} catch (IOException ee) {}
							}
						}	
						System.out.println("Item number " + item_num + " has been sold to " + to_sell.bidder_name);
						output.println("You have won item number " + item_num);
						//output.println("finished");
						out.println("Item number " + item_num + " sold successfully to " + to_sell.bidder_name + "\n");
						out.println("finished");	
						AuctionServer.ds.remove(to_sell_index); // remove item
					} // end of else 
				}// end else


			}else {
				System.out.println("The Seller entered an unrecognized command\n");
				out.println("You entered an unrecognized command");
				out.println("finished");
			}	


	
		} // end of while (true)


	}// end of deal with seller
			
	public void dealWithBuyer() {
					

		while (true) {
			String message = "";
			try {
				message = in.readLine();
			} catch (IOException e) {}
			if (message == null) {
				for (int i = 0; i < AuctionServer.ds.size(); i++) {
					if (this.id == AuctionServer.ds.get(i).bidder_id) {
						AuctionServer.ds.get(i).bidder_left = 1;
					}
				}
				try {
					this.client.close();
				} catch (IOException io) {}
				break;		
			}
			
			//decompose message
			String tokens[] = message.split(" ");

			String cmd = tokens[0];
			if (cmd.equalsIgnoreCase("login")) {
				buyer_name = tokens[1];
				System.out.println(buyer_name + " has logged in\n");	
				out.println("You have logged in as " + buyer_name);
				out.println("finished");
			}else if (cmd.equalsIgnoreCase("list")) {
				System.out.println(buyer_name + " sends the command, list\n");
				if (AuctionServer.ds.size() == 0) {
					out.println("There are no items to list");
				}
				for (int i = 0; i < AuctionServer.ds.size(); i++) {
					Record current = AuctionServer.ds.get(i);	
					out.println();
					out.println("Item: " + current.ID);
					out.println("Item name: " + current.name);
					out.println("Highest Bid: " + current.highest_bid);
					out.println("Highest Bidder: " + current.bidder_name);
					out.println();
				}
				out.println("finished");							

			}else if (cmd.equalsIgnoreCase("bid")) {
				bid(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));	

			}else {
				System.out.println(buyer_name + "  entered an unrecognized command\n");
				out.println("You entered an unrecognized command");
				out.println("finished");
			}	

		}// end of while	
		} // end deal with buyer


	public synchronized void bid(int item_num, int bid_amount) {
		Record to_bid = null;
		for (int i = 0; i < AuctionServer.ds.size(); i++) {
			if (item_num == AuctionServer.ds.get(i).ID) {
				to_bid = AuctionServer.ds.get(i);
			}
		}
		if (to_bid == null) {
			System.out.println(buyer_name + " tried to bid on a non-existing item\n");
			out.println("Item " + item_num + " does not exist");
			out.println("finished");
		}else {
			if (to_bid.bidder_id == -1) {
				System.out.println(buyer_name + " is the new highest bidder on item " + item_num + "\n");
				out.println("You are the new highest bidder on item " + item_num);
				out.println("finished");
				to_bid.highest_bid = bid_amount;
				to_bid.bidder_name = buyer_name;
				to_bid.bidder_id = this.id;
				to_bid.highest_bidder = this.client;
					

			} else if (bid_amount <= to_bid.highest_bid) {
				System.out.println(buyer_name + "'s bid was rejected, not high enough\n");
				out.println("Your bid was rejected, please bid an amount higher than current bid");
				out.println("finished");
			} else { // update with the highest bid
				PrintWriter output = null;
				Socket old_bidder = null;
				if (!buyer_name.equals(to_bid.bidder_name)) {
					old_bidder = to_bid.highest_bidder;
				}
				to_bid.highest_bid = bid_amount;
				to_bid.bidder_name = buyer_name;
				to_bid.bidder_id = this.id;
				to_bid.highest_bidder = this.client;

				System.out.println(buyer_name + " is the new highest bidder on item " + item_num + "\n");
				
				// create a printwriter and print "outbid" to old bidder
				
				if (old_bidder != null) {
				try {
					output = new PrintWriter(old_bidder.getOutputStream(), true);
				} catch (IOException io) {}

					output.println("You have been outbid on item " + item_num);
				}
				// tell the new highest bidder he is the highest bidder
				out.println("You are now the highest bidder for item " + item_num);
				out.println("finished");
			}

		}// end of else
	}// end of bid
} // end of class	
			
