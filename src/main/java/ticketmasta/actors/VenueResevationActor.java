package ticketmasta.actors;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ticketmasta.messages.ExpiredRequest;
import ticketmasta.messages.ReserveSeatsRequest;
import ticketmasta.messages.ReserveSeatsResponse;
import ticketmasta.messages.ReserveSingleSeatRequest;
import ticketmasta.messages.ReserveSingleSeatResponse;
import ticketmasta.objects.Seat;
import ticketmasta.objects.SeatHold;

public class VenueResevationActor extends VenueActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private AtomicInteger reservedSeatsCount;
	private Receive expired;
	private Receive completed;
	private SeatHold seatHold;
	private String confirmationId;
	
	public static Props props(ActorRef manager, ActorRef replyTo, SeatHold sh, int ro, int co) {
		return Props.create(VenueResevationActor.class, () -> new VenueResevationActor(manager, replyTo, sh, ro, co));
	}
	
	private VenueResevationActor(ActorRef manager, ActorRef replyTo, SeatHold sh, int ro, int co) {
		super(manager, replyTo, ro, co);
		this.reservedSeatsCount = new AtomicInteger();
		this.seatHold = sh;
		this.expired = receiveBuilder()
				.match(ReserveSeatsRequest.class, m -> {
					super.replyTo.tell(new ReserveSeatsResponse(null, false), getSelf());
				})
				.build();
		this.completed = receiveBuilder()
				.match(ReserveSeatsRequest.class, m -> {
					super.replyTo.tell(new ReserveSeatsResponse(this.confirmationId, true), getSelf());
				})
				.build();
	}

	private int getSeatHoldId() {
		return seatHold.getId();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(ReserveSeatsRequest.class, m -> {
					super.replyTo = m.getReplyTo();
					int seatHoldId = m.getSeatHoldId();
					if (seatHoldId == this.getSeatHoldId()) {
						String customerEmail = m.getCustomerEmail();
						Iterator<Seat> seatIter = this.seatHold.seatsInfoIterator();
						while (seatIter.hasNext()) {
							Seat s = seatIter.next();
							ActorSelection seatActor = this.getContext().getSystem()
									.actorSelection(MessageFormat.format("/user/manager/Seat-{0},{1}", s.getRow(), s.getColumn()));
							seatActor.tell(new ReserveSingleSeatRequest(customerEmail, seatHoldId), getSelf());
						}
					}
				})
				.match(ReserveSingleSeatResponse.class, m -> {
					boolean success = m.isSuccess();
					int numSeatsToReserve = this.seatHold.getHeldSeats().size();
					messageCount.addAndGet(1);
					if (success) {
						reservedSeatsCount.addAndGet(1);
					}
					// when aggregation is done
					if (messageCount.get() == numSeatsToReserve) {
						if (reservedSeatsCount.get() == numSeatsToReserve) {
							UUID uuid = UUID.nameUUIDFromBytes(this.seatHold.toString().getBytes());
							this.confirmationId = uuid.toString();
							super.replyTo.tell(new ReserveSeatsResponse(this.confirmationId, true), getSelf());
						} else {
							System.out.println(messageCount.get());
							super.replyTo.tell(new ReserveSeatsResponse(null, false), getSelf());
						}
						this.getContext().become(completed, true);
					}
					
				})
				.match(ExpiredRequest.class, m -> {
					this.getContext().become(expired, true);
				})
				.build();
	}
}
