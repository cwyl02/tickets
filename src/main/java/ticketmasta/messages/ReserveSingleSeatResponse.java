package ticketmasta.messages;

public class ReserveSingleSeatResponse {
	private String customerEmail;
	private int seatHoldId;
	private boolean success;
	
	public ReserveSingleSeatResponse(String customerEmail, int seatHoldId, boolean success) {
		this.customerEmail = customerEmail;
		this.seatHoldId = seatHoldId;
		this.success = success;
	}

	public int getSeatHoldId() {
		return seatHoldId;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public boolean isSuccess() {
		return success;
	}

}
