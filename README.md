# TicketMasta -- An Akka Actor-based ticket service #



### Usage ###
0. git clone the repo and cd into the repo, type
```bash
gradle build
gradle run
```
Now the app is running and waiting for you input!

1. to check numSeatsAvailable, type -check

2. to find and hold seats, type something like -findandhold a@gmail.com,5. It will either return an int:SeatholdId or complain for failure

3. to reserve the seats, type something like -reserve a@gmail.com,12345. It will either return a String:confirmationCode or complain for failure

### Why Actor? ###

* I want to re-visit this cool concept since I really like it when I first heard of it from a class about concurrency in college.
* Actor model is inherently concurrent (stolen from https://en.wikipedia.org/wiki/Actor_model). 


### Note ###

* I implemented this so that it is stateless -- no external sharing of internal states of a single actor.
*
* Since 

### Assumptions  ###

0. No data writes to disk! Application keeps running until user exits. All states are wiped out if exited.

1. Definition of "best": the closer to the center the better, the more front the better

2. Find and Hold best seats will be FAILable, which will let user to attempt again.

3. A customer can only reserve seats after he holds a bunch of seats.

4. I am praying for UUID collisions not happening.

5. I am assuming there is no message loss, which might happen in real life(heavy traffic and/or highly distributed environment?).

6. Since this is a service, the command line is not refined to a human friendly state. :(

### Thoughts ###
* The strategy to find the best seats is to be improved. Right now it tries to aggregate results from all the seats. Luckily it is still fast enough for a venue that has a capacity of 300,000. But initialization(spinning up seat actors) does take a couple more seconds to run.

* Didn't have much time to put in the definition of "best". But in real life we do. Say a group of friends definitely prefer to sit together but this application would seperate friends.

### Reference ###
1. https://doc.akka.io/docs/akka/current/actors.html?language=java
2. https://en.wikipedia.org/wiki/List_of_sporting_venues_with_a_highest_attendance_of_100,000_or_more