# TicketMasta -- An Akka Actor-based ticket service #


### Usage ###
* git clone the repo and cd into the repo, type
```bash
gradle build // build the project
gradle run
```
Now the app is running and waiting for your input!

* to check numSeatsAvailable, type -check

* to find and hold seats, type something like -hold a@gmail.com,5. It will either return an int:SeatholdId or complain for failure

* to reserve the seats, type something like -reserve a@gmail.com,12345. It will either return a String:confirmationCode or complain for failure

### Assumptions  ###

0. Application keeps running until user exits. All states are wiped out if exited.

1. Definition of "best": the closer to the center the better, the more front the better

2. Find and Hold best seats will be FAILable, which will let user to attempt again.

3. Reserve seat requests always follows a hold seat request.

4. UUIDs generated in this app are unique.

5. There is no message loss, which might happen in real life(heavy traffic and/or highly distributed environment?).

6. Since this is a service and I am expecting the input being nice. The command interface is not as robust as a real front end.


### Why Actor? ###

* I want to re-visit this cool concept since I really like it when I first heard of it from a class about concurrency in college.

* Actor model is inherently concurrent (stolen from https://en.wikipedia.org/wiki/Actor_model). 

### Workflows ###
* This just gives you an idea about how the actors interact, might be slightly inaccurate. 

* Overall: console input <-> command parser <-> service interface <-> service manager actor <-> different supervised actors

* SeatActors:
         1. has 3 different message handlers: available, held and reserved
         2. start with available 
         3. the message handler will be changed according to the message it receives
         4. will create a customerActor to keep track of holder email

* CustomerActor
         1. will terminate itself if certain message is received

* Services: 
   1. numSeatsAvailable: 
         1. service manager actor creates a VenueInquiryActor 
         2. VenueInquiryActor -> All SeatActors
         3. All SeatActors (status) -> VenueInquiryActor
         4. VenueInquiryActor (aggregate results) -> service manager actor
         
   2. findAndHoldSeats:
         1. service manager actor creates a VenueHoldingActor
         2. VenueHoldingActor -> All SeatActors
         3. All SeatActors (status) -> VenueHoldingActor
         4. VenueHoldingActor (aggregate status results) -> selected SeatActors
         5. Selected SeatActors (hold seat result) -> VenueHoldingActor
         6. VenueHoldingActor (aggregate hold seat results) -> service manage actor
         7. If holding succeeds, also creates a VenueReservationActor
         
   3. reserve:
         1. service manager -> VenueReservationActor(query via a seatHoldId)
         2. VenueReservationActor (reservation request) -> selected seatActors
         3. selected seatActors (reservation result) -> VenueReservationActor
         4. VenueReservationActor (aggregate reservation result) -> service Manager
