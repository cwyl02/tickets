package ticketmasta.services;

import java.text.MessageFormat;
import java.util.UUID;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import ticketmasta.actors.SeatActor;
import ticketmasta.actors.BoxOfficeActor;
import ticketmasta.actors.OperationManagerActor;
import ticketmasta.messages.AvailableSeatsRequestMessage;
import ticketmasta.messages.AvailableSeatsResponseMessage;
import ticketmasta.messages.SeatStatusResponseMessage;
import ticketmasta.objects.SeatHold;

public class TicketServiceActorImpl implements ITicketService {
	
	private long futureTimeout = 1000000000l;  // milliseconds
	private Duration awaitTimeout = Duration.Inf();
	private ActorSystem ticketService;
	private ActorContext context;
	private ActorRef managerActor;
	private int rows;
	private int columns;
	
	private TicketServiceActorImpl(int ro, int col) {
		this.rows = ro;
		this.columns = col;
		ticketService = ActorSystem.create("TicketMasta");
		managerActor = this.ticketService.actorOf(OperationManagerActor.props(rows, columns), "ops-manager");
		// spinning up seats
		for (int i = 0; i < ro; i++) {
			for (int j = 0; j < col; j++) {
				ticketService.actorOf(SeatActor.props(i, j, col), MessageFormat.format("Seat-{0},{1}", i, j).toString());
			}
		}
	}
	
	public ActorSystem getTicketService() {
		return ticketService;
	}
	
	public static TicketServiceActorImpl getInstance(int ro, int co) {
		return new TicketServiceActorImpl(ro, co);
	}

	private ActorSelection getSeatActors() {
		return this.ticketService.actorSelection("/user/Seat*");
	}
	
	@Override
	public int numSeatsAvailable() {
		ActorSelection seatActors = this.getSeatActors();
		String uuid = UUID.randomUUID().toString();
		//MessageFormat.format("BoxOffice-{0}", uuid)
		Future<Object> fmsg = Patterns.ask(managerActor, new AvailableSeatsRequestMessage(), futureTimeout);
		try {
			AvailableSeatsResponseMessage msg = (AvailableSeatsResponseMessage)Await.result(fmsg, awaitTimeout);
			return msg.getNumAvailableSeats();
		} catch (Exception e) {
			System.out.println("Error in numSeatsAvailable()");
			e.printStackTrace();
			return -1;
		}
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
