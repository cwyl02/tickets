package ticketmasta.objects;

import java.text.MessageFormat;
import java.util.Comparator;

import ticketmasta.utils.ISeatScore;
import ticketmasta.utils.SeatComparator;

public class Seat {
	private static SeatComparator comparator;
	private String heldBy;
	private final int row;
	private final int column;
	private final int totalColumns;
	private final SeatStatus status;
	private final ISeatScore score;
	
	
	public Seat(int ro, int co, int totalCol) {
		row = ro;
		column = co;
		totalColumns = totalCol;
		status = SeatStatus.Available;
		score = new ISeatScore () {
			@Override
			public int getScoreValue() {
				return -1 * Math.abs(column - totalColumns / 2) - row * 100;
			}
		};
	}
	
	public Seat(Seat s, SeatStatus ss, String customerEmail) {
		row = s.row;
		column = s.column;
		totalColumns = s.totalColumns;
		score = s.getScore();
		status = ss;
		heldBy = customerEmail;
	}
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public int getTotalColumns() {
		return totalColumns;
	}

	public SeatStatus getStatus() {
		return status;
	}

	public ISeatScore getScore() {
		return score;
	}
	
	public String toString() {
		return MessageFormat.format("Seat-{0},{1}", 
				new Integer(row).toString(), new Integer(column).toString());
	}
	
	public static Comparator<Seat> comparator() {
		return comparator == null? new SeatComparator() : comparator;
	}

}
