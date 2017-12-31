package ticketmasta.messages;

import ticketmasta.objects.Seat;

public class FindBestSeatsResponseMessage {
	private Seat seat;
	private String customerEmail;
	
	public FindBestSeatsResponseMessage(Seat seat, String email) {
		this.seat = seat;
		this.customerEmail = email;
	}

	public Seat getSeat() {
		return seat;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}
	
}
