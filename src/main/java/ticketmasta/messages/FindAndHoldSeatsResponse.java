package ticketmasta.messages;

import ticketmasta.objects.SeatHold;

public class FindAndHoldSeatsResponse {
	private SeatHold heldSeats;
	
	public FindAndHoldSeatsResponse(SeatHold seats) {
		heldSeats = seats;
	}

	public SeatHold getHeldSeats() {
		return heldSeats;
	}
	
}
