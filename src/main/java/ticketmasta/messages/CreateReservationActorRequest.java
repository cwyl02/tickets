package ticketmasta.messages;

import akka.actor.ActorRef;
import ticketmasta.objects.SeatHold;

public class CreateReservationActorRequest {
	private ActorRef replyTo;
    private SeatHold sh;

	public CreateReservationActorRequest(SeatHold sh, ActorRef replyTo) {
		this.sh = sh;
		this.replyTo = replyTo;
	}

	public SeatHold getSeatHold() {
		return sh;
	}

	public ActorRef getReplyTo() {
		return replyTo;
	}
}
