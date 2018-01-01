package ticketmasta.actors;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.FiniteDuration;
import ticketmasta.messages.AvailableSeatsRequest;
import ticketmasta.messages.CreateReservationActorRequest;
import ticketmasta.messages.FindAndHoldSeatsRequest;
import ticketmasta.messages.FindBestSeatsRequest;
import ticketmasta.messages.HoldExpiredRequest;
import ticketmasta.messages.ReserveSeatsRequest;
import ticketmasta.messages.SeatStatusRequest;
import ticketmasta.services.TicketServiceActorImpl;

public class ServiceManagerActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorSystem system;
	private ActorContext context;
	private final int rows;
	private final int columns;
	
	public static Props props(int ro, int co) {
		return Props.create(ServiceManagerActor.class, () -> new ServiceManagerActor(ro, co));
	}
	
	private ServiceManagerActor(int ro, int co) {
		rows = ro;
		columns = co;
	}
	
	public void preStart() {
		// spinning up seats
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.context().actorOf(SeatActor.props(i, j, columns), 
						MessageFormat.format("Seat-{0},{1}", i, j).toString());
			}
		}
		context = getContext();
		system = context.getSystem();
	}
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
				.match(AvailableSeatsRequest.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage");
					ActorRef inquiryActor = context.actorOf(VenueInquiryActor.props(getSelf(), getSender(), rows, columns), 
							MessageFormat.format("BoxOffice-A-{0}", UUID.randomUUID().toString()));
					ActorSelection seatActors = system.actorSelection("/user/manager/Seat*");
					seatActors.tell(new SeatStatusRequest(inquiryActor), getSelf());
				})
				.match(FindAndHoldSeatsRequest.class, m -> {
					log.debug("Received FindAndHoldSeatsRequestMessage");
					String uuid = UUID.randomUUID().toString();
					ActorRef bookingActor = context.actorOf(VenueHoldingActor.props(
							getSelf(), getSender(), uuid, m.getNumSeats(), rows, columns), 
							MessageFormat.format("BoxOffice-H-{0}-{1}", m.getCustomerEmail(), uuid));
					ActorSelection seatActors = system.actorSelection("/user/manager/Seat*");
					seatActors.tell(new FindBestSeatsRequest(bookingActor, m.getCustomerEmail(), m.getNumSeats()), getSelf());
				})
				.match(CreateReservationActorRequest.class, m -> { // only after a seatHold is successful
					ActorRef reservationActor = context.actorOf(VenueResevationActor.props(
							getSelf(), getSender(), m.getSeatHold(), rows, columns),
							MessageFormat.format("BoxOffice-R-{0}", m.getSeatHold().getId()));
					system.scheduler().scheduleOnce(
							new FiniteDuration(TicketServiceActorImpl.getSeatHoldExpirationTimeout(), TimeUnit.SECONDS),
							reservationActor,
							new HoldExpiredRequest(),
							system.dispatcher(),
							getSelf());
				})
				.match(ReserveSeatsRequest.class, m -> {
					ActorSelection reservationActor = context.getSystem().actorSelection(
							MessageFormat.format("/user/manager/BoxOffice-R-{0}", m.getSeatHoldId()));
					m.setReplyTo(getSender());
					reservationActor.tell(m, getSelf());
				})
				.build();
	}
}
