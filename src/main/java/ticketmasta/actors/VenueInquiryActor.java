package ticketmasta.actors;

import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import ticketmasta.messages.AvailableSeatsResponse;
import ticketmasta.messages.SeatStatusResponse;
import ticketmasta.objects.SeatStatus;

public class VenueInquiryActor extends VenueActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	private AtomicInteger seatCount;
	
	/** Constructor for an actor that takes care of finding available seats
	 * */
	public static Props props(ActorRef manager, ActorRef replyTo, int ro, int co) {
		return Props.create(VenueInquiryActor.class, () -> new VenueInquiryActor(manager, replyTo, ro, co));
	}
	
	private VenueInquiryActor(ActorRef manager, ActorRef replyTo, int ro, int co) {
		super(manager, replyTo, ro, co);
		this.seatCount = new AtomicInteger();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(SeatStatusResponse.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage message: {}", m.toString());
					messageCount.addAndGet(1);
					if (m.getStatus() == SeatStatus.Available)
						seatCount.addAndGet(1);
					if (messageCount.get() == rows * columns) {
						super.replyTo.tell(new AvailableSeatsResponse(seatCount.get()), getSelf());
						super.shutdown();
					}
				})
				.matchAny(o -> {
					log.error("Unhandled message %s%n", o.toString());
					unhandled(o);
				})
				.build();
	}

}