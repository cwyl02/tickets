package ticketmasta.messages;

import akka.actor.ActorRef;

public class FindBestSeatsRequest {
	public final ActorRef replyTo;
	private String customerEmail;
	private int numSeats;
	
	public FindBestSeatsRequest (ActorRef replyTo, String email, int numSeats) {
		this.replyTo = replyTo;
		this.customerEmail = email;
		this.numSeats = numSeats;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public int getNumSeats() {
		return numSeats;
	}
	
}
