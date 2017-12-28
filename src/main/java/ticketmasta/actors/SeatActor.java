package ticketmasta.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ticketmasta.utils.ISeatScore;

public class SeatActor extends AbstractActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	private AbstractActor.Receive held;
	private AbstractActor.Receive available;
	private AbstractActor.Receive reserved;
	
	private final int row;
	private final int column;
	private final int totalColumns;
	//TODO: better variable name
	
	static Props props(int ro, int co, int totalCol) {
		return Props.create(SeatActor.class, () -> new SeatActor(ro, co, totalCol));
	}
	
	public SeatActor(int ro, int co, int totalCol){
		row = ro;
		column = co;
		totalColumns = totalCol;
	}
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return null;
	}

}
