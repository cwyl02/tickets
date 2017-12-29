package ticketmasta.actors;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ticketmasta.messages.AvailableSeatsRequestMessage;
import ticketmasta.messages.SeatStatusResponseMessage;

public class CustomerActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	//TODO: remove
	private AbstractActor.Receive idle;
	private AbstractActor.Receive busy;
	
	private String email;
	
	public static Props props(String email) {
		return Props.create(CustomerActor.class, () -> new CustomerActor(email));
	}
	
	private CustomerActor(String email) {
		this.email = email;
		this.idle = receiveBuilder()
	        .matchEquals("foo", s -> {
	          getSender().tell("I am already angry?", getSelf());
	        })
	        .matchEquals("bar", s -> {
	          getContext().become(busy);
	        })
	        .build();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(String.class, s -> {
					log.info("Received String message: {}", s);
				})
				.match(SeatStatusResponseMessage.class, m -> {
					log.debug("Received AvailableSeatsResponseMessage message: {}", m.toString());
					
				})
				.matchAny(o -> log.info("received unknown message"))
				.build();
	}
	
}
