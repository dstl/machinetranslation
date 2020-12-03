#!/bin/bash

# Build API
cd connector-api
mvn clean package install

# Build connectors
cd ../connectors
mvn clean package install

# Build gRPC API
cd ../grpc/api
mvn clean package install

# Build gRPC Server
cd ../api-server
mvn clean package install

# Return to original directory
cd ../..
