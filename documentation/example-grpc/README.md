# Java gRPC Example

This project provides an example of how to make calls to the gRPC Machine Translation Server using the gRPC Java client.
The code is fully documented and should be referred to for details.

## Prerequisites

It is assumed that the following are installed

* Apache Maven
* JDK

### MT Api Server

To run the example, you will need a configured MT API server.
For instructions on how to configure and run this server, refer to the `api-server` project.

## Building

To build this example, run the following command from the root directory of this project.

    mvn clean package
    
This will create the `target/example-grpc-1.1.0-SNAPSHOT.jar`, which contains all the examples.
    
## Running

### Blocking Example

To run the blocking example, use the following command:

    java -jar target/example-grpc-1.1.0-SNAPSHOT.jar uk.gov.dstl.machinetranslation.examples.GrpcBlockingExample localhost 6856 "bonjour le monde"

You should see the result printed out to the console (note that for this example your MT API server must support detecting and translating French to get a translated response).

### Asynchronous Example

To run the asynchronous example, use the following command:

    java -jar target/example-grpc-1.1.0-SNAPSHOT.jar uk.gov.dstl.machinetranslation.examples.GrpcAsyncExample localhost 6856 "bonjour le monde"

You should see the result printed out to the console (note that for this example your MT API server must support detecting and translating French to get a translated response).