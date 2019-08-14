# gRPC API Server

Java server that implements the Machine Translation API and orchestrates requests to the 
configured connector for onward processing by a MT engine.

## Usage

### Configuration

The server is configured using a config file in the HOCON format used by the
[lightbend config](https://github.com/lightbend/config) library. The server
uses sensible defaults (NoOp connector, GRPC enabled on port 6856), as shown
below.

```
mt-server {
    logLevel = INFO
    connector {
        find = false
        class = NoOpConnector
    }
    grpc {
        enabled = true
        port = 6856
    }
}
```


These can be overridden using your own config file. This file need only specify the config
that you want to override, and must as a minimum contain the following:

```
include classpath("application.conf")

mt-server {
}
```

All config elements appear under the `mt-server` key. The following sections
describe each config element that appears under this key.

To use your config you must supply the following argument to the `java` command,
replacing `%PATHTOFILE%` with the obvious.

```
-Dconfig.file=%PATHTOFILE%
```

#### General Server Config

The Log Level can be set by providing the `logLevel` key, which defaults to `INFO`.
Valid levels are as supported by logback-classic (TRACE, DEBUG, INFO, WARN, ERROR).

#### Connector

Currently the server can host one connector. This can be auto-discovered on the
classpath, or set explicitly.

To auto discover an implementation `find` needs to be set to true, as below. In 
the instance that multiple connectors are found it will load one of them 
(whichever it finds first in the classpath). To enable this discovery use
the following config:

```
connector {
    find = true
}
```

To set the connector specifically `find` needs to be set to `false`, and the 
canoncial class name of the connector implementation set in `class`, as shown 
below:

```
connector {
    find = false
    class = NoOpConnector
}
```

Configuration can be provided to the connector via the config key which will be
passed as a `Map<String,Object>` to the connector, where the key is the key used in
the config file.

```
connector {
    config = {
        host = localhost
        port = 9876
    }
}
```

#### GRPC Service/Server

The gRPC endpoint is configured using the `grpc` block. This can be used to 
configure if gRPC is enabled, and the port the server runs on.

Changing the port can be done as follows.

```
grpc {
    port = 8080
}
```

### Running

To run the server the server jar is required, your own configuration file and the
jar containing the connector you wish to use. The following command gives an example
of how to then execute the server. You need to replace the arguments as appropriate
to your usage.

````
java -Dconfig.file=application.conf -cp "connector-joshua-1.0.0-shaded.jar:api-server-1.0.0.jar" uk.gov.dstl.machinetranslation.server.MtServerApplication
````

## Building

To package the server, run the following Maven command:

````
mvn package
````
