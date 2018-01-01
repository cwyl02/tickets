package ticketmasta.messages;

public class BecomeHeldRequest {
	private String email;
	
	public BecomeHeldRequest(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
