README

To build the program you'll need to install Maven. I used version 3.9.9 but other versions may work, too.
I targeted Java 23 for both source code and binary format. Later versions should also work fine.

The build command is: `mvn clean package`

To run the program: `java -jar ./target/BroadGenomics-1.0-SNAPSHOT-jar-with-dependencies.jar`

As discussed in some of the test class comments the testability of the AnswersForQuestions program is not great.
This is partly influenced by the requirement that the output be printed to the console. 
With a bit more time, I could refactor the question methods to return structures (instead of void) that have the needed data and 
then write some helper methods that write them to the console. If the requirements were
to be expanded this might be needed anyway.
