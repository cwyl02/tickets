package ticketmasta.messages;

import ticketmasta.objects.SeatHold;

public class FindAndHoldSeatsResponseMessage {
	private SeatHold heldSeats;
	
	public FindAndHoldSeatsResponseMessage(SeatHold seats) {
		heldSeats = seats;
	}

	public SeatHold getHeldSeats() {
		return heldSeats;
	}
	
}
