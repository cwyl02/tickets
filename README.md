# TicketMasta -- An Akka Actor-based ticket service #



### Usage ###
* git clone the repo and cd into the repo, type
```bash
gradle build
gradle run
```
Now the app is running and waiting for you input!

* to check numSeatsAvailable, type -check

* to find and hold seats, type something like -findandhold a@gmail.com,5. It will either return an int:SeatholdId or complain for failure

* to reserve the seats, type something like -reserve a@gmail.com,12345. It will either return a String:confirmationCode or complain for failure

### Why Actor? ###

* I want to re-visit this cool concept since I really like it when I first heard of it from a class about concurrency in college.
* Actor model is inherently concurrent (stolen from https://en.wikipedia.org/wiki/Actor_model). 

### Workflows ###

[Overall workflow](https://www.draw.io/?lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&title=Untitled%20Diagram.xml#R7VrLcqM4FP0aL%2BNCvL103MlMV3VXpSqL6VlNKSDb6sbIJeTE6a8fCSSMHk6Ig3GmJiwSuOjFPeceXUmeBIvN%2Fg8Kt%2BvvJEfFxPfy%2FST4MvH9WTjjf4XhuTFEQBpWFOeNCRwM9%2Fg3kkZPWnc4R5VWkBFSMLzVjRkpS5QxzQYpJU96sSUp9F63cIUsw30GC9v6F87ZurGmkXew%2F4nwaq16Bp588wCzXytKdqXsb%2BIHy%2FpqXm%2BgakuWr9YwJ08dU3AzCRaUENbcbfYLVAjXKrc19W6PvG3HTVHJ%2BlTwmwqPsNghNeJ6XOxZ%2BYIPcStu8aZ22vWabQr%2BCPjtI6IMc6%2FNC7wquY2Rbcf6DT6g4o5UmGEi3j4QxsiGFyjEi%2BvWUwtSEFr3pXwVXNedzattA67HLVA9LPEe5aoIf14zJlgxFx%2Fp32Z5GU4x58USlzmi04z36N%2FmkEH%2BT9ir5n%2FGHUTR1ZIidKXMIOHOul1e8TobWOb%2F%2BFF8Bfx0ui1XvL8cVmvRcf3lS1wUatwlKYVflqRkkskglc%2BdT%2FPqi9ulx7mT0P4oaqDlAg8xRDaI0WdeRFaI46aGjC6QSjY9Hbgapo1p3aGpskEZHau24QND%2BI0kiZswgUWYiuW4tFjDP47pZKkYJb%2BQ6TTbj1CSSSCEqINlG5znopvrpzVm6H4LM9HnExcibqsZVcPkWZiNB1Di6QCFDoA8GyClCu8BKH49olGZz4VICicXsKpwpiPV11Foj9kPWUfc%2Fy28Po3EEw%2Bu5x8ShPrh8K4ZDsotBTacy4dMdjRDmlAxSFdIlpq5Iej4OHL4WNkoKiDDj%2FogXI6XPdwRzIfXItxCKiFu8VRNNIOXtboKbDY0M7gyMxpqPtlqqOZB%2B9m9qDF7nRp6%2BLgCrMuT00WwEQM1twbDRF0bP8qVILTCrp2pu5xoBfQ9gadyl457F81Ewo13kFZczD6mSo4ojHxKNZTRhsh3TF1gCGkEwAJAyNC9fCSUrcmKlLC4OVgNV3WQOmjcNIm6Mide%2FkSMPUufwh0j3HRo%2FhsRedIJc9Lromk7%2Fp06N0t1vKLgNJl7rZ3hVA70yGnfJHO2Vl02%2BwtVzLwkc8Alc%2BbUckoQJZZ37xF9xNx3vvdVaNFS%2BPFdDh8q03Zq6RAARDMdgDi1AEgd%2Fo%2BH0DA7AbdFbeD8Tt3LHC7xu%2BoHJlqG10ghL3yHKOZfJ%2BamL29RMD3tk47upn2%2BG5tx0j4jsQ%2FMxKF31geM9NHkxoB6GI7PmJdWBFOPa4jGmTBKleFsrAEXpQ3wdcluQXkrb7ivX25oQN5EF1CaqefNdO6k4aCCclkeWPCp6fStPAh8s6H4bDx4acb%2FDku4Egsbb54xYi9w1ObhblM0BYbZHnRvPH7gnC02d4Rix45QZPNuiIWpK27fsexpQzXqBuqRIO3IvrZG8o4J%2FqUWToZKJI4ZJHEj3FsSeuN1gRRPn7BDEL%2BKpz6FB8Y17HyulnR94BhHyAMwUCIYxEZDwdkSQX%2FERLA%2Fzh8nSTM12vdOxDRODbGfJWfDtE%2BSVhR4WwlRV7NxVpBd%2Ft%2Fb%2BDCTnsAxiUaujY8g9AeQZftkReY83vVn3nNsq8Q43QjV0WUHssCB2BBbJY6tKgXY%2FBOwI4BFkQ5YpARlDMDSo4AtPgE7trLwjAgLRwTMPlGcNpcF1%2F%2FupCtOIj2QIhuXc%2F0GQOE9ygqicwwWGpvBIG0Npy8EtL3fyO3zkRJE4xDLWp33TRAToHMjON%2FubwBGpEK7NZB4agEpiRAng%2B7iXZYH5ilAGA20ULAIdTIP%2BOPhR4RN8cMPNYObfwE%3D)

Get num seats workflow

Find and hold seats workflow

Reserve seats workflow

### Highlights ###

* I implemented this so that it is stateless -- no external sharing of internal states of a single actor.
I took advantage of Akka actor's become method to change seat actor's behavior during runtime(v.s in oop, a function/method behaves differently depending on the incoming state, which might be mutable.)

* Hold by customer state is implemented by attach an external 

* The command line interface is not refined and you might find it annoying. Sorry folks! :(

### Assumptions  ###

0. No data writes to disk! Application keeps running until user exits. All states are wiped out if exited.

1. Definition of "best": the closer to the center the better, the more front the better

2. Find and Hold best seats will be FAILable, which will let user to attempt again.

3. A customer can only reserve seats after he holds a bunch of seats.

4. UUIDs generated in this app are unique.

5. There is no message loss, which might happen in real life(heavy traffic and/or highly distributed environment?).

### Thoughts ###
* The strategy to find the best seats is to be improved. Right now it tries to aggregate results from all the seats. Luckily it is still fast enough for a venue that has a capacity of 300,000. But initialization(spinning up seat actors) does take a couple more seconds to run.

* Didn't have much time to put in the definition of "best". But in real life we do. Say a group of friends definitely prefer to sit together but this application would seperate friends.

* Yes. You don't want to look at how many massage class out there...

### Reference ###
1. https://doc.akka.io/docs/akka/current/actors.html?language=java
2. https://en.wikipedia.org/wiki/List_of_sporting_venues_with_a_highest_attendance_of_100,000_or_more