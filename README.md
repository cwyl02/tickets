# TicketMasta -- An Akka Actor-based ticket service #


### Why Actor? ###

* Actor model is inherently concurrent (stolen from https://en.wikipedia.org/wiki/Actor_model) 
* I want to re-visit this cool concept since I really like it when I first heard of it from a class about concurrency in college.

### Assumptions  ###

1. Definition of "best": the closer to the center the better, the more front the better

2. Find and Hold best seats will be FAILable, which will let user to attempt again.

3. A customer can only reserve seats after he holds a bunch of seats.

4. I am praying for UUID collisions not happening.

5. I am assuming there is no message loss, which might happen in real life.


### Thoughts ###
* The strategy to find the best seats is to be improved. Right now it tries to aggregate results from all the seats. Luckily it is fast enough, because I am initialize a venue that has a capacity of 300,000, which exceeds the seats of Indianapolis Motor Speedway.

### Reference ###
1. https://doc.akka.io/docs/akka/current/actors.html?language=java
2. https://en.wikipedia.org/wiki/List_of_sporting_venues_with_a_highest_attendance_of_100,000_or_more