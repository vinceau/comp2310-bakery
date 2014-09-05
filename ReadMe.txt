***ReadMe file for COMP2310 Assignment 1, 2014***

Name: Vincent Au
StudentId: u5388374

***Disclaimer***:
  The work that I am submitting for this assignment is without significant
  contributions from others.

***Names and Brief Description of Parallel Compositions in Bakery.lts ***
The parallel composition BAKERY is the FSP for both the Simple system and
Intermediate I system. Due to the LTSA tool having issues compiling
multiple parallel compositions, this parallel composition is commented out
in favour of BAKERY2 which is the FSP for the Intermediate II. You can
uncomment BAKERY and comment BAKERY2 to swap systems. The process SERVER2
and the process CHECK and are exclusively used in BAKERY2 as a part of the 
Intermediate II system.
The values NC, NS, and NB are universal and are used regardless of which 
system you run.

***Design Decisions in Modelling, and ANY pertinent Observations***
I initially attempted to have the ticket number wrap as a part of the
CUSTOMER take action but this made things ridiculously complicated. Hence,
I began to simplify things and give processes a dedicated purpose and
nothing more.

***Design Decisions in Implementation***
I used a Queue to manage the next ticket number to be called. This made the
implementation a lot easier to manage. I made the Order class to easily
manage which server is assisting which customer, and how many buns etc.

***Notable Deficiencies***
The names in the LTS model do not match the names of the Java Bakery. This 
could be fixed by additional renaming but it's not that big of an issue.
The Bakery program works with the range of numbers specified in the spec,
but when a negative NB is specified the program fails to detect this and
executes it as normal.

***Extra Comments***
If bonus points can be awarded:
I have identified issues initially present in Peter Strazdin's BakeryParam 
class.
(https://cs.anu.edu.au/streams/forum.php?MsgID=66801)
I also wrote up a Bakery testing script and released it to people allowing 
them to test their code before Peter's tester was complete.
(https://cs.anu.edu.au/streams/forum.php?MsgID=66898)

***Feedback from undertaking this assignment*** (optional, not marked)

