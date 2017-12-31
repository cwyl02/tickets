package ticketmasta.messages;

public class HoldSeatRequestMessage {
	private final String customerEmail;
	
	public HoldSeatRequestMessage(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

}
