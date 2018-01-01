package ticketmasta.messages;

public class ReserveSeatsResponse {
	private String confirmationCode;
	private boolean success;
	
	public ReserveSeatsResponse(String confirmationCode, boolean success) {
		super();
		this.confirmationCode = confirmationCode;
		this.success = success;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public boolean isSuccess() {
		return success;
	}
}
