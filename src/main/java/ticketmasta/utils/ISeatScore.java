package ticketmasta.utils;

public interface ISeatScore {
	/**
	* The score of the seat. Seats with higher scores will always be preferred.
	*
	* @return the number of tickets available in the venue
	*/
	int getSeatScore();
}
