package ticketmasta.actors;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.collection.GenTraversableOnce;
import scala.concurrent.duration.FiniteDuration;

import ticketmasta.messages.HoldSeatRequest;
import ticketmasta.messages.HoldSeatResponse;
import ticketmasta.messages.ReserveSingleSeatRequest;
import ticketmasta.messages.ReserveSingleSeatResponse;
import ticketmasta.messages.BecomeAvailableRequest;
import ticketmasta.messages.BecomeHeldRequest;
import ticketmasta.messages.FindBestSeatsRequest;
import ticketmasta.messages.FindBestSeatsResponse;
import ticketmasta.messages.ExpiredRequest;
import ticketmasta.messages.SeatStatusRequest;
import ticketmasta.messages.SeatStatusResponse;
import ticketmasta.objects.Seat;
import ticketmasta.objects.SeatStatus;
import ticketmasta.services.TicketServiceActorImpl;

public class SeatActor extends AbstractActor {

	private static final FiniteDuration holdExpiration = new FiniteDuration(
			TicketServiceActorImpl.getSeatHoldExpirationTimeout(), TimeUnit.SECONDS);
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorSystem actorSystem;
	private Scheduler scheduler;
	private Receive held;
	private Receive available;
	private Receive reserved;
	private Seat seat;
	
	public static Props props(int ro, int co, int totalCol) {
		return Props.create(SeatActor.class, () -> new SeatActor(ro, co, totalCol));
	}
	
	public SeatActor(int ro, int co, int totalCol){
		this.seat = new Seat(ro, co, totalCol);
		/*  the behavior when it is available
		 * */
		available = receiveBuilder()
				.match(SeatStatusRequest.class, m -> { // sent from service manager actor
					log.debug("Received SeatStatusRequestMessage: {}", m.toString());
					ActorRef inquiryActor = m.replyTo;
					inquiryActor.tell(new SeatStatusResponse(SeatStatus.Available), getSelf());
				})
				.match(FindBestSeatsRequest.class, m -> { // sent from service manager actor
					log.debug("Received FindBestSeatsRequestMessage message: {}", m.toString());
					ActorRef holdingActor = m.replyTo;
					if (!this.getContext().getChildren().iterator().hasNext())
						holdingActor.tell(new FindBestSeatsResponse(seat, m.getCustomerEmail()), getSelf());
					else
						holdingActor.tell(new FindBestSeatsResponse(null, m.getCustomerEmail()), getSelf());
				})
				.match(HoldSeatRequest.class, m -> { // sent from booking actor
					log.debug("Received HoldSeatRequestMessage message: {}", m.toString());
					ActorRef holdingActor = getSender();
					holdingActor.tell(
							new HoldSeatResponse(m.getCustomerEmail(), seat, true), 
							getSelf());
					// transform to behave like held
					getSelf().tell(new BecomeHeldRequest(m.getCustomerEmail()), getSelf());
				})
				.match(BecomeHeldRequest.class, m -> {
					ActorRef customerActor = this.getContext().actorOf(CustomerActor.props(m.getEmail()), 
							MessageFormat.format("Customer-{0}", m.getEmail()));
					// tell the child to terminate after the time out
					this.scheduleSendingMessage(this.scheduler, holdExpiration, customerActor, new ExpiredRequest());
					// swap back to available after seatHold expires
					this.scheduleSendingMessage(this.scheduler, holdExpiration, getSelf(), new BecomeAvailableRequest());
					this.getContext().become(held, true);
				})
				.build();
		
		/* the behavior when it is held. 
		It is added to the behavior stack so common handler for FindBestSeatsRequestMessage it is using the one specified in available
		*/ 
		held = receiveBuilder()
				.match(SeatStatusRequest.class, m -> { // sent from service manager actor
					log.debug("Received SeatStatusRequestMessage: {}", m.toString());
					ActorRef inquiryActor = m.replyTo;
					inquiryActor.tell(new SeatStatusResponse(SeatStatus.Held), getSelf());
				})
				.match(FindBestSeatsRequest.class, m -> { // sent from service manager actor
					ActorRef holdingActor = m.replyTo;
					holdingActor.tell(new FindBestSeatsResponse(null, m.getCustomerEmail()), getSelf());
				})
				.match(HoldSeatRequest.class, m -> { // sent from booking actor
					log.debug("Received HoldSeatRequestMessage message: {}", m.toString());
					ActorRef bookingActor = getSender();
					bookingActor.tell(
							new HoldSeatResponse(m.getCustomerEmail(), seat, false), 
							getSelf());
				})
				.match(BecomeAvailableRequest.class, m -> {
					this.getContext().become(available, true);
				})
				.match(ReserveSingleSeatRequest.class, m -> {
					String customerEmail = m.getCustomerEmail();
					// should only have at most one
					Iterator<ActorRef> customerIter = this.getContext().getChildren().iterator();
					ActorRef heldBy;
					if (customerIter.hasNext()) {
						heldBy = customerIter.next();
						heldBy.tell(new ReserveSingleSeatRequest(customerEmail, m.getSeatHoldId()), getSelf());
					} else {
						getSender().tell(new ReserveSingleSeatResponse(customerEmail, m.getSeatHoldId(), false), getSelf());
					}
				})
				.match(ReserveSingleSeatResponse.class, m -> {
					ActorSelection reservationActor = this.getContext().getSystem().actorSelection(
							MessageFormat.format("/user/manager/BoxOffice-R-{0}", Integer.toString(m.getSeatHoldId())));
					reservationActor.tell(m, getSelf());
					if (m.isSuccess()) {
						this.getContext().become(reserved, true);
					}
				})
				.build();
		
		/* the behavior when it is reserved. Will be applied on top of held handler.
		 */
		reserved = receiveBuilder()
				.match(SeatStatusRequest.class, m -> { // sent from service manager actor
					log.debug("Received SeatStatusRequestMessage: {}", m.toString());
					ActorRef inquiryActor = m.replyTo;
					inquiryActor.tell(new SeatStatusResponse(SeatStatus.Reserved), inquiryActor);
				})
				.match(FindBestSeatsRequest.class, m -> { // sent from service manager actor
					ActorRef holdingActor = m.replyTo;
					holdingActor.tell(new FindBestSeatsResponse(null, m.getCustomerEmail()), getSelf());
				})
				.match(BecomeAvailableRequest.class, m -> { 
					log.debug("Already reserved ignoring this BecomeAvailableRequest...%n");
				})
				.match(ReserveSingleSeatRequest.class, m -> {
					getSender().tell(new ReserveSingleSeatResponse(m.getCustomerEmail(), m.getSeatHoldId(), false), getSelf());
				})
				.build();
	}
	
	private void scheduleSendingMessage(Scheduler s, FiniteDuration t, ActorRef receiver, Object msg) {
		s.scheduleOnce(t, receiver, msg, this.actorSystem.dispatcher(), getSelf());
	}
	
	/* When the actor is initializing
	 * */
	@Override
	public void preStart() {
		this.actorSystem = this.getContext().getSystem();
		this.scheduler = this.actorSystem.scheduler();
		this.getContext().become(available, true);// true means clear the behavior stack
	}
	
	/** Place holder. It will be substituted to available/held/reserved handler specified above
	 * during runtime depends on the "state" of this actor.
	 * */
	@Override
	public Receive createReceive() {
		return receiveBuilder().build();
	}
	
	@Override
	public String toString(){
		return seat.toString();
	}

}
