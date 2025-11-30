README

To build the program you'll need to install Maven. I used version 3.9.9 but other versions may work, too.
I targeted Java 23 for both source code and binary format. Later versions should also work fine.

The build command is: `mvn clean package`

To skip running unit tests: `mvn clean package -DskipTests=true`

If you prefer to just run the unit tests: `mvn test`

~~Note that I haven't integrated an API key so you may (read will likely) see throttling. The logging will make it clear
when this happens.~~ I've added API key support. To use it set your API key as an environment variable called MBTA_API_KEY.
For example:

`export MBTA_API_KEY="c02fd4d6a3d242bb8d3576b3eb5fd5cc"`

If you'd prefer to skip this you can run specific unit tests e.g : 
    `mvn test -Dtest=AnswersForQuestionsTest#questionOne`

You can also run each of the unit test classes separately:
    `mvn test -Dtest=APIClientTest`

To run the main program: `java -jar ./target/BroadGenomics-1.0-SNAPSHOT-jar-with-dependencies.jar`

No doubt there's something I've forgotten :(
 