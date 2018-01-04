import java.util.Scanner;

import ticketmasta.command.Command;
import ticketmasta.objects.SeatHold;
import ticketmasta.services.ITicketService;
import ticketmasta.services.TicketServiceActorImpl;

public class App {
    public static String getGreeting() {
        return "Welcome to TicketMasta! Type '-help' to start";
    }

    public static void main(String[] args) {
        System.out.println(getGreeting());
        ITicketService ticketMasta = TicketServiceActorImpl.getInstance(100, 300);
        Scanner scanner = new Scanner(System.in); 
        try {
	        	while (true) {
	        		Command.processCommand(ticketMasta, scanner.nextLine());	
	        }
        } catch (Exception e) {
        		e.printStackTrace();
        } finally {
        		scanner.close();
        		ticketMasta.shutdown();
        }
    }
}
