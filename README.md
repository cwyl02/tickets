# TicketMasta -- An Akka Actor-based ticket service #


### Why Actor? ###

* Actor model is inherently concurrent (stolen from https://en.wikipedia.org/wiki/Actor_model) 
* I want to re-visit this cool concept since I really like it when I first heard of it from a class about concurrency in college.

### Assumptions  ###

* Definition of "best": the closer to the center the better, the more front the better
* If the application is modified to running on a REAL concurrent environment.(Right now all the actions are based on a sequence of commands.)
1. Find and Hold best seats will be FAILable, which will let user to attempt again. :(
2. 


### Thoughts ###
* Akka has a become/unbecome mechanism that can swap the createReceive() event handler during runtime.
It wipes out the need to maintain a seat status if we make use of that(if feasible.)
But I didn't figure out how to take advantage of this to handle the state transition.

* At the very last, thank you guys for giving me this opportunity! Since this is the first time I write a java application from the ground up. 

* Any feedbacks regarding to this project only are highly appreciated.
