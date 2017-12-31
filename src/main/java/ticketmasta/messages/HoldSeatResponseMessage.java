package ticketmasta.messages;

public class HoldSeatResponseMessage {
	private final String customerEmail;
	private final boolean success;
	private int row;
	private int column;
	
	public HoldSeatResponseMessage(String customerEmail, int row, int column, boolean success) {
		this.customerEmail = customerEmail;
		this.row = row;
		this.column = column;
		this.success = success;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public boolean isSuccess() {
		return success;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
}
