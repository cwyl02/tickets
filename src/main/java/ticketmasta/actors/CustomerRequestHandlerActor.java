package ticketmasta.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CustomerRequestHandlerActor extends AbstractActor{
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return null;
	}

}
