package ticketmasta.actors;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.StablePriorityQueue;
import ticketmasta.messages.FindAndHoldSeatsResponseMessage;
import ticketmasta.messages.HoldSeatRequestMessage;
import ticketmasta.messages.HoldSeatResponseMessage;
import ticketmasta.messages.FindBestSeatsResponseMessage;
import ticketmasta.objects.*;

public class VenueBookingActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	private ActorRef manager;
	private ActorRef replyTo;
	private StablePriorityQueue<Seat> bestSeats;
	private SeatHold seatHold;
	private String customerEmail;
	private AtomicInteger messageCount;
	private int rows;
	private int columns;
	private int numSeatsRequested;
	
	public static Props props(ActorRef manager, ActorRef replyTo, String email, int numSeats, int ro, int co) {
		return Props.create(VenueBookingActor.class, () -> new VenueBookingActor(manager, replyTo, email, numSeats, ro, co));
	}
	
	private VenueBookingActor(ActorRef manager, ActorRef replyTo, String email, int numSeats, int ro, int co) {
		this.manager = manager;
		this.replyTo = replyTo;
		this.customerEmail = email;
		this.seatHold = new SeatHold();
		this.bestSeats = new StablePriorityQueue<Seat>(numSeats, Seat.comparator());
		this.numSeatsRequested = numSeats;
		this.rows = ro;
		this.columns = co;
		this.messageCount = new AtomicInteger();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(FindBestSeatsResponseMessage.class, m -> {
					log.debug("Received FindBestSeatsResponseMessage message: {}", m.toString());
					messageCount.addAndGet(1);
					Seat seat = m.getSeat();
					if (seat.getStatus() == SeatStatus.Available) {
						this.bestSeats.add(seat);
					}
					if (messageCount.get() == rows * columns) {
						for (int i = 0; i < this.numSeatsRequested; i++) {
							Seat bestSeat = this.bestSeats.poll();
							ActorSelection seatActor = getContext().getSystem()
									.actorSelection(MessageFormat.format("/user/Seat-{0},{1}", 
											bestSeat.getRow(), bestSeat.getColumn()).toString());
							seatActor.tell(new HoldSeatRequestMessage(m.getCustomerEmail()), getSelf());
						}
						messageCount.lazySet(0);// for hold seat reponse counting
					}
				})
				.match(HoldSeatResponseMessage.class, m -> {
					log.info("Received HoldSeatResponseMessage message: {}%n", m);
					boolean success = m.isSuccess();
					messageCount.addAndGet(1);
					if (success) {
						this.seatHold.holdSeat(MessageFormat.format("Seat-{0},{1}", m.getRow(), m.getColumn()));
					}
					if (messageCount.get() == numSeatsRequested) {
						log.info(MessageFormat.format("Successfully held {0} seats!%n", numSeatsRequested));
						this.seatHold.setSuccess(true);;
						this.replyTo.tell(new FindAndHoldSeatsResponseMessage(this.seatHold), getSelf());
						this.getContext().stop(getSelf()); // terminate this actor
					}
				})
				.matchAny(o -> log.info("received unknown message"))
				.build();
	}
	
}
