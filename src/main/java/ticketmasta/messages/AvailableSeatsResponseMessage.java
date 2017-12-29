package ticketmasta.messages;

public class AvailableSeatsResponseMessage {
	private int numAvailableSeats;
	
	public AvailableSeatsResponseMessage(int num) {
		numAvailableSeats = num;
	}

	public int getNumAvailableSeats() {
		return numAvailableSeats;
	}
	
}
