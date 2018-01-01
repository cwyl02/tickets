package ticketmasta.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SeatHold {
	private List<Seat> heldSeats;
	private int id;
	private boolean success;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public SeatHold() {
		heldSeats = new ArrayList<Seat>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void holdSeat(Seat s) {
		heldSeats.add(s);
	}
	
	public List<Seat> getHeldSeats() {
		return heldSeats;
	}

	public Iterator<Seat> seatsInfoIterator() {
		return this.heldSeats.iterator();
	}
	
	@Override
	public String toString() {
		return success ? new Integer(id).toString() : "failed to hold seats";
	}
}
