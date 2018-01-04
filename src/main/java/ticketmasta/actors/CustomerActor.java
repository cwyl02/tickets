package ticketmasta.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import ticketmasta.messages.ExpiredRequest;
import ticketmasta.messages.ReserveSingleSeatRequest;
import ticketmasta.messages.ReserveSingleSeatResponse;

public class CustomerActor extends AbstractActor {

	private String customerEmail;
	
	public static Props props(String email) {
		return Props.create(CustomerActor.class, () -> new CustomerActor(email));
	}
	
	private CustomerActor(String email) {
		this.customerEmail = email;
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(ExpiredRequest.class, m -> {
					this.shutdown();
				})
				.match(ReserveSingleSeatRequest.class, m -> {
					String requestedCustomerEmail = m.getCustomerEmail();
					if (requestedCustomerEmail.equals(this.customerEmail)) {
						getSender().tell(new ReserveSingleSeatResponse(requestedCustomerEmail, m.getSeatHoldId(), true), 
								getSelf());
					} else {
						getSender().tell(new ReserveSingleSeatResponse(requestedCustomerEmail, m.getSeatHoldId(), false), 
								getSelf());
					}
				})
				.build();
	}

	private void shutdown() {
		getContext().getSystem().stop(getSelf());
	}
	
	public String getCustomerEmail() {
		return customerEmail;
	}

}
