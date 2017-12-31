package ticketmasta.objects;

import java.util.ArrayList;
import java.util.List;

public class SeatHold {
	private List<String> heldSeats;
	private int id;
	private boolean success;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public SeatHold() {
		heldSeats = new ArrayList<String>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void holdSeat(String s) {
		heldSeats.add(s);
	}
	
	@Override
	public String toString() {
		return success ? heldSeats.toString() : "failed to hold seats";
	}
}
