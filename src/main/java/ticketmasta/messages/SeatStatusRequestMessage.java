package ticketmasta.messages;

import akka.actor.ActorRef;

public class SeatStatusRequestMessage {
	public final ActorRef replyTo;
	
	public SeatStatusRequestMessage(ActorRef replyTo) {
		this.replyTo = replyTo;
	}
}
