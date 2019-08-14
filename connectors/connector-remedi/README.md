# REMEDI Connector

This connector allows you to use [REMEDI](https://github.com/ivan-zapreev/Distributed-Translation-Infrastructure/) to perform translation via the Machine Translation Connector API.

The following configuration is required (default values show below):

    {
      "translationServer": "ws://localhost:9090",
      "preProcessingServer": null,
      "postProcessingServer": null
    }

You should set `translationServer` to point at your REMEDI translation server or load balancer;
`preProcessingServer` and `postProcessingServer` are optional (use `null` for unset),
and should point towards the pre- and post-processing servers respectively if you wish to use them.


The configuration map should be passed to the `configure()` function of the connector.