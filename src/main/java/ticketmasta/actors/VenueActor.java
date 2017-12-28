package ticketmasta.actors;

import java.text.MessageFormat;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ticketmasta.messages.AvailableSeatsRequestMessage;

public class VenueActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private int rows;
	private int columns;
	
	public static Props props(Integer ro, Integer co) {
		return Props.create(VenueActor.class, () -> new VenueActor(ro, co));
	}
	
	public VenueActor(Integer ro, Integer co) {
		this.rows = ro;
		this.columns = co;
	} 
	
	/* The "constructor" to initialize the VenueActor
	 * It is invoked upon ticketService.actorOf(Props.create(VenueActor.class)) in main
	 * */
	@Override
	public void preStart() throws Exception {
		super.preStart();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				getContext().actorOf(SeatActor.props(i, j, columns), MessageFormat.format("{}, {}", i, j));
			}
		}
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(String.class, s -> {
					log.info("Received String message: {}", s);
				})
				.match(AvailableSeatsRequestMessage.class, m -> {
					log.debug("Received AvailableSeatsRequestMessage message: {}", m.toString());
					
				})
				.matchAny(o -> log.info("received unknown message"))
				.build();
	}

}