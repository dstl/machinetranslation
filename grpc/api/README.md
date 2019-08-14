# Machine Translation (MT) API

This project describes the gRPC API used to communciate with the API Server (`api-server`), and provides tools to build clients for the API.
Note that this is distinct (though similar in structure) to the Connector API used to directly communciate with MT tools, which is specified in the `connector-api` project.

The API is specified in Protobuf format, and can be found at `src/main/proto/machine_translation.proto`.

## Description of API

There are 4 methods on the API, which are briefly described as follows:

* **SupportedLanguages** - Returns a list of the supported language pairs
* **IdentifyLanguage** - Identifies the language of a string
* **Translate** - Translates a string between two languages
* **QueryEngine** - Returns information about the MT tool

For full details on the API, please refer to the Protobuf definition.

## Building Server and Client in Java

To automatically generate a Java gRPC Client and Server for this API, run the following command:

    mvn package

This will produce `target/grpc-api-1.0.0.jar`, which can be included within your projects. The Maven coordinates for this will be:

    <dependency>
      <groupId>uk.gov.dstl.machinetranslation</groupId>
      <artifactId>grpc-api</artifactId>
    </dependency>

For more information about how to use the Java API, refer to the [gRPC Java documentation](https://grpc.io/docs/tutorials/basic/java).

## Building Server and Client in Python

To automatically generate a Python gRPC Client and Server for this API, run the following commands:

    pip install grpcio-tools
    mkdir target
    python -m grpc_tools.protoc -Isrc/main/proto --python_out=target src/main/proto/machine_translation.proto

This will produce `target/machine_translation_pb2.py`, which can be used within oyur Python scripts. For more information about how to use the Python API, refer to the [gRPC Python documentation](https://grpc.io/docs/tutorials/basic/python).

## Building Server and Client in Other Languages

It is possible to generate gRPC Client and Server code in a number of languages. Refer to the [gRPC documentation](https://grpc.io/docs/tutorials/) for full details.
