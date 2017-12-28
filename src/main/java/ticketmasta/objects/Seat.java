package ticketmasta.objects;



public class Seat {
	public enum Status {
		Available, Held, Reserved
	}
	
	private int row;
	private int column;
	private Status seatStatus;
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public Status getSeatStatus() {
		return seatStatus;
	}
	public void setSeatStatus(Status seatStatus) {
		this.seatStatus = seatStatus;
	}
	
	
}
