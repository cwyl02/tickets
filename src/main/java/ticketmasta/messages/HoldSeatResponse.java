package ticketmasta.messages;

import ticketmasta.objects.Seat;

public class HoldSeatResponse {
	private final String customerEmail;
	private final boolean success;
	private Seat seat;
	
	public HoldSeatResponse(String customerEmail, Seat seat, boolean success) {
		this.customerEmail = customerEmail;
		this.seat = seat;
		this.success = success;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public boolean isSuccess() {
		return success;
	}

	public Seat getSeat() {
		return seat;
	}
	
}
