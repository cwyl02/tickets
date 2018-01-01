package ticketmasta.messages;

import ticketmasta.objects.SeatStatus;

public class SeatStatusResponse {
	private SeatStatus status;
	
	public SeatStatusResponse(SeatStatus status) {
		this.status = status;
	}

	public SeatStatus getStatus() {
		return status;
	}

}
