package ticketmasta.messages;

import akka.actor.ActorRef;

public class SeatStatusRequest {
	public final ActorRef replyTo;
	
	public SeatStatusRequest(ActorRef replyTo) {
		this.replyTo = replyTo;
	}
}
