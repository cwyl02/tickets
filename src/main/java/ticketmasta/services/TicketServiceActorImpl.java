package ticketmasta.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import ticketmasta.actors.ServiceManagerActor;
import ticketmasta.messages.AvailableSeatsRequest;
import ticketmasta.messages.AvailableSeatsResponse;
import ticketmasta.messages.FindAndHoldSeatsRequest;
import ticketmasta.messages.FindAndHoldSeatsResponse;
import ticketmasta.messages.ReserveSeatsRequest;
import ticketmasta.messages.ReserveSeatsResponse;
import ticketmasta.objects.SeatHold;

public class TicketServiceActorImpl implements ITicketService {
	
	private ActorSystem ticketService;
	private ActorRef managerActor;
	private Duration awaitTimeout = Duration.Inf();
	private long futureTimeout = 15000l;  // milliseconds
	private static int seatHoldExpirationTimeout = 30; // default seat hold expiration, in seconds
	private int rows;
	private int columns;
	
	public static int getSeatHoldExpirationTimeout() {
		return seatHoldExpirationTimeout;
	}

	/* For tests
	 * */
	public static void setSeatHoldExpirationTimeout(int seatHoldExpirationTimeout) {
		TicketServiceActorImpl.seatHoldExpirationTimeout = seatHoldExpirationTimeout;
	}

	private TicketServiceActorImpl(int ro, int col) {
		this.rows = ro;
		this.columns = col;
		this.ticketService = ActorSystem.create("TicketMasta");
		this.managerActor = this.ticketService.actorOf(ServiceManagerActor.props(rows, columns), "manager");
	}
	
	public ActorSystem getTicketService() {
		return ticketService;
	}
	
	public static TicketServiceActorImpl getInstance(int ro, int co) {
		return new TicketServiceActorImpl(ro, co);
	}
	
	@Override
	public int numSeatsAvailable() {
		Future<Object> fmsg = Patterns.ask(managerActor, new AvailableSeatsRequest(), futureTimeout);
		try {
			AvailableSeatsResponse msg = (AvailableSeatsResponse)Await.result(fmsg, awaitTimeout);
			return msg.getNumAvailableSeats();
		} catch (Exception e) {
			System.out.println("Error in numSeatsAvailable()");
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		Future<Object> fmsg = Patterns.ask(managerActor, new FindAndHoldSeatsRequest(customerEmail, numSeats), futureTimeout);
		try {
			FindAndHoldSeatsResponse msg = (FindAndHoldSeatsResponse)Await.result(fmsg, awaitTimeout);
			SeatHold sh = msg.getHeldSeats(); 
			if (sh.isSuccess()) {
				return sh;
			} else 
				return null;
		} catch (Exception e) {
			System.out.println("Error in findAndHoldSeats");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		Future<Object> fmsg = Patterns.ask(managerActor, new ReserveSeatsRequest(seatHoldId, customerEmail), futureTimeout);
		try {
			ReserveSeatsResponse msg = (ReserveSeatsResponse)Await.result(fmsg, awaitTimeout);
			String ret;
			if (msg.isSuccess())
				ret = msg.getConfirmationCode();
			else
				ret = null;
			return ret;
		} catch (Exception e) {
			System.out.println("Error in reserveSeats");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void shutdown() {
		ticketService.terminate();
	}

}
