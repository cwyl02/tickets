package ticketmasta.messages;

import akka.actor.ActorRef;

public class FindAndHoldSeatsRequestMessage {
	private String customerEmail;
	private int numSeats;

	public FindAndHoldSeatsRequestMessage(String email, int numSeats) {
		this.customerEmail = email;
		this.numSeats = numSeats;
	}
	
	public int getNumSeats() {
		return numSeats;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}
}
