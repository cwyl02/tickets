package ticketmasta.command;

import java.text.MessageFormat;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ticketmasta.objects.SeatHold;
import ticketmasta.services.ITicketService;

public class Command {
	
	private static Options options;
	private static Option help, check, hold, reserve, exit;
	private static CommandLineParser parser;
	static {
		options = new Options();
        
        help = new Option("help", "print this message");
        check = new Option("check", "get number of available seats");
        exit = new Option("exit", "exit the application");
        hold = Option.builder("hold")
        						.argName("email,numseats")
        						.required(false)
        						.hasArgs()
        						.numberOfArgs(2)
        						.valueSeparator(',')
        						.desc("e.g. dummy@gmail.com,5")
        						.build();
        reserve = Option.builder("reserve")
        						.argName("email,seatholdid")
        						.required(false)
        						.hasArg()
        						.numberOfArgs(2)
        						.valueSeparator(',')
        						.desc("e.g., dummy@gmail.com, 12345")
        						.build();
        options.addOption(help);
        options.addOption(check);
        options.addOption(hold);
        options.addOption(reserve);
        options.addOption(exit);
        parser = new DefaultParser();
	}

	private static String parseEmailAddress(Object value) throws ParseException {
		String emailInput = (String) value;
		try {
			InternetAddress emailAddr = new InternetAddress(emailInput);
			emailAddr.validate();
		} catch (AddressException ex) {
			throw new ParseException("Invalid format of email address!");
		}
		return emailInput;
	}
	
	private static int parseIntegerInput(Object value) throws ParseException {
		int ret;
		try {
			ret = Integer.parseInt((String) value);
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid number of seats to hold");
		}
		return ret;
	}
	
	/**
	 * @param service the ticket service instance
	 * @param the string read from console
	 *
	 * */
	@SuppressWarnings("rawtypes")
	public static void processCommand(ITicketService service, String input) {
		String[] args = input.split(" ");
		CommandLine line;
		try {
				line = parser.parse(options, args);
				if (line.hasOption("help")) {
					HelpFormatter formatter = new HelpFormatter();
					formatter.printHelp( "TicketMasta", options);
				} else 
					if (line.hasOption("check")) {
					int numAvailableSeats = service.numSeatsAvailable();
					System.out.println(MessageFormat.format("Available seats: {0}", numAvailableSeats));
				} else 
					if (line.hasOption("hold")) {
					Properties map = line.getOptionProperties("hold");
					Entry e = map.entrySet().iterator().next();
					String customerEmail = parseEmailAddress(e.getKey());
					int numSeatsRequested = parseIntegerInput(e.getValue());
					SeatHold sh = service.findAndHoldSeats(numSeatsRequested, customerEmail);
					if (sh == null)
						System.out.println("Failed to hold your seats! Please try again.");
					else {
						System.out.println(
								MessageFormat.format(
										"{0} is successfully to find and hold {1} seats. Your seatHold id is: {2}", 
										customerEmail, numSeatsRequested, Integer.toString(sh.getId())));
					}
				} else 
					if (line.hasOption("reserve")) {
					Properties map = line.getOptionProperties("reserve");
					Entry e = map.entrySet().iterator().next();
					String customerEmail = parseEmailAddress(e.getKey());
					int seatHoldId = parseIntegerInput(e.getValue());
					String confirmationId = service.reserveSeats(seatHoldId, customerEmail);
					if (confirmationId == null) {
						System.out.println("Failed to reserve your seats! Please try hold seats and reserve again.");
					} else {
						System.out.println(
								MessageFormat.format("Successfully to reserve your seats. Your confirmation id is: {0}", 
										confirmationId)
						);
					}
				} else if (line.hasOption("exit")) {
					System.out.println("Bye!");
					System.exit(0);
				} 
		} catch (ParseException e) {
			System.out.println("Malformed input");
			String msg = e.getMessage();
			if (msg != null)
				System.out.println(msg);
		}	
	}
}
