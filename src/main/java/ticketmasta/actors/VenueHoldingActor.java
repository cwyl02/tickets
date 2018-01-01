package ticketmasta.actors;

import java.text.MessageFormat;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.StablePriorityQueue;
import ticketmasta.messages.CreateReservationActorRequest;
import ticketmasta.messages.FindAndHoldSeatsResponse;
import ticketmasta.messages.HoldSeatRequest;
import ticketmasta.messages.HoldSeatResponse;
import ticketmasta.messages.FindBestSeatsResponse;
import ticketmasta.objects.*;

public class VenueHoldingActor extends VenueActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	private StablePriorityQueue<Seat> bestSeats;
	private SeatHold seatHold;
	private String uuid;
	private int numSeatsRequested;
	
	public String getUuid() {
		return uuid;
	}
	
	public static Props props(ActorRef manager, ActorRef replyTo, String uuid, int numSeats, int ro, int co) {
		return Props.create(VenueHoldingActor.class, () -> new VenueHoldingActor(manager, replyTo, uuid, numSeats, ro, co));
	}
	
	private VenueHoldingActor(ActorRef manager, ActorRef replyTo, String uuid, int numSeats, int ro, int co) {
		super(manager, replyTo, ro, co);
		this.uuid = uuid;
		this.seatHold = new SeatHold();
		this.bestSeats = new StablePriorityQueue<Seat>(numSeats, Seat.comparator());
		this.numSeatsRequested = numSeats;
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(FindBestSeatsResponse.class, m -> {
					log.debug("Received FindBestSeatsResponseMessage message: {}", m.toString());
					messageCount.addAndGet(1);
					Seat seat = m.getSeat();
					if (seat != null) {
						this.bestSeats.add(seat);
					}
					if (messageCount.get() == rows * columns) {
						for (int i = 0; i < this.numSeatsRequested; i++) {
							Seat bestSeat = this.bestSeats.poll();
							ActorSelection seatActor = getContext().getSystem()
									.actorSelection(MessageFormat.format("/user/manager/Seat-{0},{1}", 
											bestSeat.getRow(), bestSeat.getColumn()).toString());
							seatActor.tell(new HoldSeatRequest(m.getCustomerEmail()), getSelf());
						}
						messageCount.lazySet(0);// clear count for hold seat reponse counting
					}
				})
				.match(HoldSeatResponse.class, m -> {
					log.debug("Received HoldSeatResponseMessage message: {}%n", m);
					boolean success = m.isSuccess();
					messageCount.addAndGet(1);
					if (success) {
						this.seatHold.holdSeat(m.getSeat());
					}
					if (messageCount.get() == numSeatsRequested) {
						int heldSeatsCount = this.seatHold.getHeldSeats().size();
						if ( heldSeatsCount == numSeatsRequested) {
							log.debug(MessageFormat.format("Successfully held {0} seats!%n", numSeatsRequested));
							this.seatHold.setSuccess(true);;
							this.seatHold.setId((int)Math.floor(Math.random() * rows * columns));
							// create a venue reservation Actor that waits for reservation request
							super.manager.tell(new CreateReservationActorRequest(this.seatHold, this.replyTo), getSelf());
						} else {
							log.info(MessageFormat.format("Only able to held {0} seats, need to hold {1} seats, please try again%n",
									heldSeatsCount, numSeatsRequested));
							this.seatHold.setSuccess(false);
							this.seatHold.setId(-1);
						}
						super.replyTo.tell(new FindAndHoldSeatsResponse(this.seatHold), getSelf());
						super.shutdown(); // terminate this actor
					}
				})
				.matchAny(o -> log.error("received unknown message"))
				.build();
	}
	
}
