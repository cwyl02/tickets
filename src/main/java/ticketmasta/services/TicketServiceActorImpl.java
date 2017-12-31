package ticketmasta.services;

import java.text.MessageFormat;
import java.util.HashMap;
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
import ticketmasta.actors.VenueInquiryActor;
import ticketmasta.actors.VenueBookingActor;
import ticketmasta.actors.ServiceManagerActor;
import ticketmasta.messages.AvailableSeatsRequestMessage;
import ticketmasta.messages.AvailableSeatsResponseMessage;
import ticketmasta.messages.FindAndHoldSeatsRequestMessage;
import ticketmasta.messages.FindAndHoldSeatsResponseMessage;
import ticketmasta.messages.SeatStatusResponseMessage;
import ticketmasta.objects.SeatHold;

public class TicketServiceActorImpl implements ITicketService {
	
	private ActorSystem ticketService;
	private ActorRef managerActor;
	private Duration awaitTimeout = Duration.Inf();
	private HashMap<String, ActorRef> customerBookingActorMap;
	private long futureTimeout = 10000l;  // milliseconds
	private static int seatHoldExpirationTimeout;
	private int rows;
	private int columns;
	
	public static int getSeatHoldExpirationTimeout() {
		return seatHoldExpirationTimeout;
	}

	public static void setSeatHoldExpirationTimeout(int seatHoldExpirationTimeout) {
		TicketServiceActorImpl.seatHoldExpirationTimeout = seatHoldExpirationTimeout;
	}

	private TicketServiceActorImpl(int ro, int col) {
		this.rows = ro;
		this.columns = col;
		this.ticketService = ActorSystem.create("TicketMasta");
		this.managerActor = this.ticketService.actorOf(ServiceManagerActor.props(rows, columns), "ops-manager");
		// spinning up seats
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.ticketService.actorOf(SeatActor.props(i, j, col), 
						MessageFormat.format("Seat-{0},{1}", i, j).toString());
			}
		}
		this.customerBookingActorMap = new HashMap<String, ActorRef>();
	}
	
	public ActorSystem getTicketService() {
		return ticketService;
	}
	
	public static TicketServiceActorImpl getInstance(int ro, int co) {
		seatHoldExpirationTimeout = 5; // default seat hold expiration
		return new TicketServiceActorImpl(ro, co);
	}

	private ActorSelection getSeatActors() {
		return this.ticketService.actorSelection("/user/Seat*");
	}
	
	@Override
	public int numSeatsAvailable() {
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
		Future<Object> fmsg = Patterns.ask(managerActor, new FindAndHoldSeatsRequestMessage(customerEmail, numSeats), futureTimeout);
		try {
			FindAndHoldSeatsResponseMessage msg = (FindAndHoldSeatsResponseMessage)Await.result(fmsg, awaitTimeout);
			return msg.getHeldSeats();
		} catch (Exception e) {
			System.out.println("Error in findAndHoldSeats");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		ticketService.terminate();
	}

}
