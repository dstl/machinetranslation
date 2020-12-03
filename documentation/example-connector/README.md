# Java Connector API Example

This project provides an example of how to use the Machine Translation Connector API.
The code is fully documented and should be referred to for details.

## Prerequisites

It is assumed that the following are installed

* Apache Maven
* JDK

In addition, it is assumed you have installed the `connector-api` project, which can be done by running the following command
from the root directory of the `connector-api` project:

    mvn install

### Connectors

This example will run as is, using the `NoOpConnector`.
To make the most of the example though, you should consider modifying it to use a proper implementation of the Connector API.
These are available in the `connectors/connector-*` projects.

## Building and Running

To build this example, run the following command from the root directory of this project.

    mvn clean package
    
This will create the `target/example-connector-1.1.0-SNAPSHOT.jar` JAR, which can be executed using:

    java -jar target/example-connector-1.1.0-SNAPSHOT.jar ConnectorExample "ciao mondo"

You will see some output being printed to the console.
Note that the `NoOpConnector` does not perform any translation, and therefore you should not be surprised that the output is the same as the input!