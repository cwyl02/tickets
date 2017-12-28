package ticketmasta.services;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import ticketmasta.actors.VenueActor;
import ticketmasta.objects.SeatHold;

public class TicketServiceImpl implements ITicketService {
	
	private ActorSystem ticketService;
	private ActorRef venue;
	private ActorContext context;
	
	private TicketServiceImpl(int ro, int co) {
		ticketService = ActorSystem.create("TicketMasta");
		venue = ticketService.actorOf(Props.create(VenueActor.class));
		
	}
	
	public static TicketServiceImpl getInstance(int ro, int co) {
		return new TicketServiceImpl(ro, co);
	}

	
	
	@Override
	public int numSeatsAvailable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
