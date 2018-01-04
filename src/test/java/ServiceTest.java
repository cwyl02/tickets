import org.junit.Test;

import junit.framework.TestCase;
import ticketmasta.objects.SeatHold;
import ticketmasta.services.ITicketService;
import ticketmasta.services.TicketServiceActorImpl;

public class ServiceTest extends TestCase{
	private ITicketService s;
	private String customerEmail;
	private int rows, columns, numSeatsToHold, expirationTimeout;
	
	@Override
	protected void setUp() {
		customerEmail = "bitcoin@moon.gov";
		rows = 10;
		columns = 100;
		numSeatsToHold = 5;
		expirationTimeout = 2;
		TicketServiceActorImpl.setSeatHoldExpirationTimeout(expirationTimeout);
	}
	
	@Override
	protected void tearDown() {
		s.shutdown();
	}
	
	@Test
	public void testNumSeatsAvailableBasic() {
		s = TicketServiceActorImpl.getInstance(rows, columns);
		assertEquals(rows*columns, s.numSeatsAvailable());
	}
	
	@Test
	public void testFindAndHoldBasic() {
		s = TicketServiceActorImpl.getInstance(rows, columns);
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
	
	@Test
	public void testReserveSeatBasic() {
		s = TicketServiceActorImpl.getInstance(rows, columns);
		SeatHold sh = s.findAndHoldSeats(numSeatsToHold, customerEmail);
		assertNotNull(sh);
		String confirmationCode = s.reserveSeats(sh.getId(), customerEmail);
		assertNotNull(confirmationCode);
		try {
			Thread.sleep(TicketServiceActorImpl.getSeatHoldExpirationTimeout() * 1000 + 15);// need some time to count for messaging overhead
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(rows*columns - numSeatsToHold, s.numSeatsAvailable());
	}
	

	@Test
	public void testFindAndHoldFailSimple() {
		s = TicketServiceActorImpl.getInstance(rows, columns);
		SeatHold sh = s.findAndHoldSeats(rows*columns, customerEmail);
		assertNotNull(sh);
		String confirmationCode = s.reserveSeats(sh.getId(), customerEmail);
		assertNotNull(confirmationCode);
		SeatHold sh2 = s.findAndHoldSeats(1, customerEmail);
		assertNull(sh2);
	}
	
	@Test
	public void testReserveFailSimple() {
		s = TicketServiceActorImpl.getInstance(rows, columns);
		SeatHold sh = s.findAndHoldSeats(rows*columns, customerEmail);
		assertNotNull(sh);
		String confirmationCode = s.reserveSeats(sh.getId(), "bitcoin@pizza.org");
		assertNull(confirmationCode);
	}
}
