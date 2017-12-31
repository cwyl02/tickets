package ticketmasta.utils;

import java.util.Comparator;

import ticketmasta.objects.Seat;

public class SeatComparator implements Comparator<Seat> {

	@Override
	public int compare(Seat o1, Seat o2) {
		return new Integer(o1.getScore().getScoreValue())
				.compareTo(o2.getScore().getScoreValue()) * -1; // default order is ascending, we want it descending.
	}
	
}