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
import ticketmasta.messages.FindAndHoldSeatsRequestMessage;
import ticketmasta.messages.FindBestSeatsRequestMessage;
import ticketmasta.messages.SeatStatusRequestMessage;

public class ServiceManagerActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorContext context;
	private final int rows;
	private final int columns;
	
	public ServiceManagerActor(int ro, int co) {
		context = getContext();
		rows = ro;
		columns = co;
	}
	
	public static Props props(int ro, int co) {
		return Props.create(ServiceManagerActor.class, () -> new ServiceManagerActor(ro, co));
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
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
				.match(AvailableSeatsRequestMessage.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage");
					ActorRef inquiryActor = context.actorOf(VenueInquiryActor.props(getSelf(), getSender(), rows, columns), 
							MessageFormat.format("BoxOffice-{0}", UUID.randomUUID().toString()));
					ActorSelection seatActors = context.getSystem().actorSelection("/user/Seat*");
					seatActors.tell(new SeatStatusRequestMessage(inquiryActor), getSelf());
				})
				.match(FindAndHoldSeatsRequestMessage.class, m -> {
					log.debug("Received FindAndHoldSeatsRequestMessage");
					ActorRef bookingActor = context.actorOf(VenueBookingActor.props(getSelf(), getSender(), m.getCustomerEmail(), m.getNumSeats(), rows, columns), 
							MessageFormat.format("BoxOffice-{0}", UUID.randomUUID().toString()));
					ActorSelection seatActors = context.getSystem().actorSelection("/user/Seat*");
					seatActors.tell(new FindBestSeatsRequestMessage(bookingActor, m.getCustomerEmail(), m.getNumSeats()), getSelf());
				})
				.build();
	}
}
