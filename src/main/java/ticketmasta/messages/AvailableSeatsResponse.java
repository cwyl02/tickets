package ticketmasta.messages;

public class AvailableSeatsResponse {
	private int numAvailableSeats;
	
	public AvailableSeatsResponse(int num) {
		numAvailableSeats = num;
	}

	public int getNumAvailableSeats() {
		return numAvailableSeats;
	}
	
}
