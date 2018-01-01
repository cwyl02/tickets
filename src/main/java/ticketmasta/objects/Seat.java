package ticketmasta.objects;

import java.text.MessageFormat;
import java.util.Comparator;

import ticketmasta.utils.ISeatScore;
import ticketmasta.utils.SeatComparator;

public class Seat {
	private static SeatComparator comparator;
	private final int row;
	private final int column;
	private final int totalColumns;
	private final ISeatScore score;
	
	public Seat(int ro, int co, int totalCol) {
		row = ro;
		column = co;
		totalColumns = totalCol;
		score = new ISeatScore () {
			@Override
			public int getScoreValue() {
				return -1 * Math.abs(column - totalColumns / 2) - row * 100;
			}
		};
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
