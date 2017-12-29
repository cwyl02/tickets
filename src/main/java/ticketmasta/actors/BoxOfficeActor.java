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
import ticketmasta.messages.SeatStatusResponseMessage;
import ticketmasta.messages.SeatStatusRequestMessage;
import ticketmasta.objects.SeatStatus;

public class BoxOfficeActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	private ActorRef manager;
	private ActorRef replyTo;
	private AtomicInteger seatCount;
	private AtomicInteger messageCount;
	private int rows;
	private int columns;
	
	public static Props props(ActorRef manager, ActorRef replyTo, int ro, int co) {
		return Props.create(BoxOfficeActor.class, () -> new BoxOfficeActor(manager, replyTo, ro, co));
	}
	
	private BoxOfficeActor(ActorRef manager, ActorRef replyTo, int ro, int co) {
		this.manager = manager;
		this.replyTo = replyTo;
		this.rows = ro;
		this.columns = co;
		this.seatCount = new AtomicInteger();
		this.messageCount = new AtomicInteger();
	} 
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				
				.match(SeatStatusResponseMessage.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage message: {}", m.toString());
					messageCount.addAndGet(1);
					if (m.getStatus() == SeatStatus.Available)
						seatCount.addAndGet(1);
					log.debug("curr seatCount" + new Integer(seatCount.get()).toString());
					if (messageCount.get() == rows * columns) {
						this.replyTo.tell(new AvailableSeatsResponseMessage(seatCount.get()), getSelf());
					}
					
				})
				.match(HoldSeatRequestMessage.class, m -> {
					log.info("Received HoldSeatRequestMessage message: {}", m);
					
				})
				.matchAny(o -> {
					log.error("Unhandled message %s%n", o.toString());
					unhandled(o);
				})
				.build();
	}

}