package ticketmasta.actors;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.UUID;

import akka.actor.AbstractActor;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ticketmasta.messages.AvailableSeatsRequestMessage;
import ticketmasta.messages.AvailableSeatsResponseMessage;
import ticketmasta.messages.SeatStatusRequestMessage;

public class OperationManagerActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorContext context;
	private final int rows;
	private final int columns;
	
	public OperationManagerActor(int ro, int co) {
		context = getContext();
		rows = ro;
		columns = co;
	}
	
	public static Props props(int ro, int co) {
		return Props.create(OperationManagerActor.class, () -> new OperationManagerActor(ro, co));
	}
	
	/**
	 * reply sends given message back to sender of current message.
	 * 
	 * @param msg	Message to send back.
	 */
	private void reply (Object msg) {
		getSender().tell(msg, getSelf());
	}
	
	@Override
	public void preStart() {
		
	}
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
				.match(AvailableSeatsRequestMessage.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage");
					ActorRef boxOffice = context.actorOf(BoxOfficeActor.props(getSelf(), getSender(), rows, columns), MessageFormat.format("BoxOffice-{0}", UUID.randomUUID().toString()).toString());
					ActorSelection seatActors = context.getSystem().actorSelection("/user/Seat*");
					seatActors.tell(new SeatStatusRequestMessage(boxOffice), getSelf());
				})
				.build();
	}
}
