package ticketmasta.messages;

public class ReserveSingleSeatRequest {
	private String customerEmail;
	private int seatHoldId;
	
	public ReserveSingleSeatRequest(String customerEmail, int seatHoldId) {
		this.customerEmail = customerEmail;
		this.seatHoldId = seatHoldId;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public int getSeatHoldId() {
		return seatHoldId;
	}
	
}
