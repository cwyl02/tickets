package ticketmasta.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ticketmasta.messages.AvailableSeatsRequestMessage;
import ticketmasta.messages.HoldSeatRequestMessage;
import ticketmasta.messages.SeatStatusRequestMessage;
import ticketmasta.messages.SeatStatusResponseMessage;
import ticketmasta.objects.SeatStatus;
import ticketmasta.utils.ISeatScore;

public class SeatActor extends AbstractActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	private Receive held;
	private Receive available;
	private Receive reserved;
	
	private final int row;
	private final int column;
	private final int totalColumns;
	//TODO: better variable name
	
	public static Props props(int ro, int co, int totalCol) {
		return Props.create(SeatActor.class, () -> new SeatActor(ro, co, totalCol));
	}
	
	public SeatActor(int ro, int co, int totalCol){
		row = ro;
		column = co;
		totalColumns = totalCol;
		
		available = receiveBuilder()
				.match(SeatStatusRequestMessage.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage message: {}", m.toString());
					ActorRef sender = m.replyTo;
					sender.tell(new SeatStatusResponseMessage(SeatStatus.Available), sender);
				})
				.match(HoldSeatRequestMessage.class, m -> {
					log.debug("Received HoldSeatRequestMessage message: {}", m.toString());
					getContext().become(held);
				})
				.build();
		
		held = receiveBuilder()
				.match(SeatStatusRequestMessage.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage message: {}", m.toString());
					ActorRef sender = m.replyTo;
					sender.tell(new SeatStatusResponseMessage(SeatStatus.Held), sender);
				})
				.matchAny(m -> {
					log.error("Unhandled message %s%n", m.toString());
					unhandled(m);
				})
				.build();
		
		reserved = receiveBuilder()
				.match(SeatStatusRequestMessage.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage message: {}", m.toString());
					ActorRef sender = m.replyTo;
					sender.tell(new SeatStatusResponseMessage(SeatStatus.Reserved), sender);
				})
				.matchAny(m -> {
					log.error("Unhandled message %s%n", m.toString());
					unhandled(m);
				})
				.build();
	}
	
	/** Initialize the seat
	 * */
	@Override
	public void preStart() {
		getContext().become(available, true);
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(HoldSeatRequestMessage.class, m -> {
					log.debug("Received HoldSeatRequestMessage message: {}", m.toString());
					getContext().become(held);
				})
				.build();
	}

}
