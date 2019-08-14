# SYSTRAN Connector

This connector allows you to use [SYSTRAN](https://platform.systran.net/) to perform translation via the Machine Translation Connector API.

The following configuration is required (default values show below):

    {
      "apiKey": null,
      "basePath": null
    }

The `apiKey` should be a server API key with no origin limitations. If set to `null`, no authentication will be used.

The `basePath` should point to the SYSTRAN server. If set to `null`, then the SYSTRAN.io service will be used.

The configuration map should be passed to the `configure()` function of the connector.

## Running the integration tests

To build this connector with the integration tests, you must pass a valid API key for SYSTRAN.io as a system property. For example:

    mvn verify -DsystranApiKey=...