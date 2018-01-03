import org.junit.Test;

import junit.framework.TestCase;
import ticketmasta.objects.SeatHold;
import ticketmasta.services.ITicketService;
import ticketmasta.services.TicketServiceActorImpl;

public class ServiceTest extends TestCase{
	private ITicketService s;
	
	@Override
	protected void tearDown() {
		s.shutdown();
	}
	
	@Test
	public void testNumSeatsAvailableBasic() {
		int rows = 10, columns = 100;
		s = TicketServiceActorImpl.getInstance(rows, columns);
		assertEquals(rows*columns, s.numSeatsAvailable());
	}
	
	@Test
	public void testFindAndHoldBasic() {
		int rows = 10, columns = 100, numSeatsToHold = 25, expirationTimeout = 2;
		String customerEmail = "bitcoin@moon.gov";
		s = TicketServiceActorImpl.getInstance(rows, columns);
		TicketServiceActorImpl.setSeatHoldExpirationTimeout(expirationTimeout);
		SeatHold sh = s.findAndHoldSeats(numSeatsToHold, customerEmail);
		assertNotNull(sh);
		assertEquals(numSeatsToHold, sh.getHeldSeats().size());
		assertEquals(rows*columns - numSeatsToHold, s.numSeatsAvailable());
		try {
			Thread.sleep(TicketServiceActorImpl.getSeatHoldExpirationTimeout() * 1000 + 15);// need some time to count for messaging overhead
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(rows*columns, s.numSeatsAvailable());
	}
	
	
	
	
}
