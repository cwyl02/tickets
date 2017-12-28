package ticketmasta.messages;

import akka.actor.ActorRef;

public class AvailableSeatsRequestMessage {
	private final ActorRef replyTo;
	
	public AvailableSeatsRequestMessage(ActorRef replyTo) {
		this.replyTo = replyTo;
	}
}
