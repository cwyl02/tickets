package ticketmasta.messages;

import akka.actor.ActorRef;

public class ReserveSeatsRequest {
	private ActorRef replyTo;
	private String customerEmail;
	private int seatHoldId;
	
	public ReserveSeatsRequest(int sid, String custEmail) {
		this.seatHoldId = sid;
		this.customerEmail = custEmail;
	}

	public int getSeatHoldId() {
		return seatHoldId;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public ActorRef getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(ActorRef replyTo) {
		this.replyTo = replyTo;
	}
	
}
