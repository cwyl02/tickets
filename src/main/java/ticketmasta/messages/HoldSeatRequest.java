package ticketmasta.messages;

public class HoldSeatRequest {
	private final String customerEmail;
	
	public HoldSeatRequest(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

}
