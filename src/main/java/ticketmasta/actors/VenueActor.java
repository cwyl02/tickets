package ticketmasta.actors;

import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

public abstract class VenueActor extends AbstractActor {
	protected ActorRef manager;
	protected ActorRef replyTo;
	protected AtomicInteger messageCount;
	protected int rows;
	protected int columns;
	
	protected VenueActor(ActorRef manager, ActorRef replyTo, int ro, int co) {
		this.manager = manager;
		this.replyTo = replyTo;
		this.messageCount = new AtomicInteger();
		this.rows = ro;
		this.columns = co;
	}
	
	protected void shutdown() {
		this.getContext().stop(getSelf());
	};
}
