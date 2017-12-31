package ticketmasta.actors;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ticketmasta.messages.AvailableSeatsRequestMessage;
import ticketmasta.messages.AvailableSeatsResponseMessage;
import ticketmasta.messages.HoldSeatRequestMessage;
import ticketmasta.messages.FindBestSeatsResponseMessage;
import ticketmasta.messages.SeatStatusResponseMessage;
import ticketmasta.messages.SeatStatusRequestMessage;
import ticketmasta.objects.Seat;
import ticketmasta.objects.SeatHold;
import ticketmasta.objects.SeatStatus;

public class VenueInquiryActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	private ActorRef manager;
	private ActorRef replyTo;
	private SeatHold seatHold;
	private String customerEmail;
	private AtomicInteger seatCount;
	private AtomicInteger messageCount;
	private int rows;
	private int columns;
	
	/** Constructor for an actor that takes care of booking related actions 
	 * */
	private VenueInquiryActor(ActorRef manager, ActorRef replyTo, int ro, int co, String email) {
		this.manager = manager;
		this.replyTo = replyTo;
		this.customerEmail = email;
		this.seatHold = new SeatHold();
		this.rows = ro;
		this.columns = co;
		this.seatCount = new AtomicInteger();
		this.messageCount = new AtomicInteger();
	}
	
	/** access from outside
	 * */
	public static Props props(ActorRef manager, ActorRef replyTo, int ro, int co, String email) {
		return Props.create(VenueInquiryActor.class, () -> new VenueInquiryActor(manager, replyTo, ro, co, email));
	}
	
	/** Constructor for an actor that takes care of finding available seats
	 * */
	public static Props props(ActorRef manager, ActorRef replyTo, int ro, int co) {
		return Props.create(VenueInquiryActor.class, () -> new VenueInquiryActor(manager, replyTo, ro, co));
	}
	
	/** access constructor from outside
	 * */
	private VenueInquiryActor(ActorRef manager, ActorRef replyTo, int ro, int co) {
		this.manager = manager;
		this.replyTo = replyTo;
		this.rows = ro;
		this.columns = co;
		this.seatCount = new AtomicInteger();
		this.messageCount = new AtomicInteger();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(SeatStatusResponseMessage.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage message: {}", m.toString());
					messageCount.addAndGet(1);
					if (m.getStatus() == SeatStatus.Available)
						seatCount.addAndGet(1);
					if (messageCount.get() == rows * columns) {
						this.replyTo.tell(new AvailableSeatsResponseMessage(seatCount.get()), getSelf());
						this.getContext().stop(getSelf());
					}
				})
				.matchAny(o -> {
					log.error("Unhandled message %s%n", o.toString());
					unhandled(o);
				})
				.build();
	}

}