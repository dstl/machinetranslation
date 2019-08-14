# Moses Connector

This connector allows you to use [Moses](http://www.statmt.org/moses/) to perform translation via the Machine Translation Connector API.

The following configuration is required (default value show below):

    {
      "rpc": "http://localhost:8080/RPC2"
    }

You should set `rpc` to point at the RPC end point for your Moses server, and pass the map to the `configure()` function of the connector.

Note that this connector ignores the `sourceLanguage` and `targetLanguage` values of translation requests,
as each instance of the Moses server will only translate between a single language pair.

## Running the Integration Test

To run the integration test, you must have an instance of Moses server running on http://localhost:8080.
The integration test can then be run with:

    mvn verify