# Apache Joshua Connector

This connector allows you to use [Apache Joshua](https://joshua.apache.org/) to perform translation via the Machine Translation Connector API.

The following configuration is required (default value show below):

    {
      "server": "http://localhost:5674"
    }

You should set `server` to point at your instance of Joshua, and pass the map to the `configure()` function of the connector.

Note that this connector ignores the `sourceLanguage` and `targetLanguage` values of translation requests,
as each instance of the Joshua server will only translate between a single language pair.

## Running the Integration Test

To run the integration test, you must have an instance of Joshua running on http://localhost:5674.
The integration test can then be run with:

    mvn verify