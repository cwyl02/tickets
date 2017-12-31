package ticketmasta.actors;

import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.FiniteDuration;

import ticketmasta.messages.HoldSeatRequestMessage;
import ticketmasta.messages.HoldSeatResponseMessage;
import ticketmasta.messages.BecomeHeldMessage;
import ticketmasta.messages.FindBestSeatsRequestMessage;
import ticketmasta.messages.FindBestSeatsResponseMessage;
import ticketmasta.messages.SeatStatusRequestMessage;
import ticketmasta.messages.SeatStatusResponseMessage;
import ticketmasta.objects.Seat;
import ticketmasta.objects.SeatStatus;
import ticketmasta.services.TicketServiceActorImpl;

public class SeatActor extends AbstractActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorSystem actorSystem;
	private Receive held;
	private Receive available;
	private Receive reserved;
	
	private Seat seat;
	//TODO: better variable name
	
	public static Props props(int ro, int co, int totalCol) {
		return Props.create(SeatActor.class, () -> new SeatActor(ro, co, totalCol));
	}
	
	public SeatActor(int ro, int co, int totalCol){
		seat = new Seat(ro, co, totalCol);
	}
	
	@Override
	public void preStart() {
		this.actorSystem = this.getContext().getSystem();
		this.getContext().become(available);
	}
	
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(SeatStatusRequestMessage.class, m -> { // sent from service manager actor
					log.debug("Received AvailableSeatsRequestMessage message: {}", m.toString());
					ActorRef inquiryActor = m.replyTo;
					inquiryActor.tell(new SeatStatusResponseMessage(seat.getStatus()), inquiryActor);
				})
				.match(FindBestSeatsRequestMessage.class, m -> { // sent from service manager actor
					log.debug("Received PreHoldSeatRequestMessage message: {}", m.toString());
					ActorRef bookingActor = m.replyTo;
					bookingActor.tell(new FindBestSeatsResponseMessage(seat, m.getCustomerEmail()), bookingActor);
				})
				.match(HoldSeatRequestMessage.class, m -> { // sent from booking actor
					log.debug("Received HoldSeatRequestMessage message: {}", m.toString());
					ActorRef bookingActor = getSender();
					if (this.seat.getStatus() == SeatStatus.Available) {
						seat = new Seat(seat, SeatStatus.Held, m.getCustomerEmail());
						bookingActor.tell(
								new HoldSeatResponseMessage(m.getCustomerEmail(), seat.getRow(), seat.getColumn(), true), 
								getSelf());
						// "transform" back to held
						getSelf().tell(new BecomeHeldMessage(), getSelf());
					} else {
						bookingActor.tell(
								new HoldSeatResponseMessage(m.getCustomerEmail(), seat.getRow(), seat.getColumn(), false), 
								getSelf());
					}
				})
				.match(BecomeHeldMessage.class, m -> {
					this.getContext().become(held);
					this.actorSystem.scheduler().scheduleOnce(
							new FiniteDuration(TicketServiceActorImpl.getSeatHoldExpirationTimeout(), TimeUnit.SECONDS), 
							getSelf(), 
							new BecomeHeldMessage(), 
							this.actorSystem.dispatcher(), 
							getSelf());
				})
				.build();
	}
	
	@Override
	public String toString(){
		return seat.toString();
	}

}
