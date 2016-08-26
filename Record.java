// John Paschal G00861791
// Songyue Huang G00864305

import java.io.*;
import java.net.*;

public class Record {

public int ID;
public String name;
public int highest_bid;
public Socket highest_bidder;
public String bidder_name;
public int bidder_left;
public int bidder_id;

	public Record(int id, String name) {
		ID = id;
		this.name = name;
		highest_bid = 0;	
		bidder_name = "no bids";
		bidder_left = 0;
		bidder_id = -1;
	}

}		
		


	
