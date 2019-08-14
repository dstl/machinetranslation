# Machine Translation Connector API

This project defines a common interface for communicating with Machine Translation tools, known as the Connector API.
Additionally, a number of helper utilities are provided to reduce code duplication of common tasks (including testing);
and a NoOpConnector is provided for testing and fallback.

For full information about the API, refer to the Javadoc.

## Implementation

When developing a new connector, you should implement the `MTConnectorApi` interface.

You should also ensure that your connector is compatible with the Java ServiceLoader,
by providing the following file with the fully qualified name of your class as its content.

    src/main/resources/META-INF/services/uk.gov.dstl.machinetranslation.connector.api.MTConnectorApi

## Building

To build the API, run the following command from the `connector-api` directory:

    mvn package