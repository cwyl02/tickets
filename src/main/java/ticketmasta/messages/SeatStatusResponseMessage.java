package ticketmasta.messages;

import ticketmasta.objects.SeatStatus;

public class SeatStatusResponseMessage {
	private SeatStatus status;
	
	public SeatStatusResponseMessage(SeatStatus status) {
		this.status = status;
	}

	public SeatStatus getStatus() {
		return status;
	}

}
