# Machine Translation API Server - Docker Image

The Dockerfile builds a container image used to run the mt-api-server component and 
an associated connector. 

The `api-server` component is baked into the image at build time, to assist with,
versioning and release. When building the image the version of the `mt-api-server`
component you require (e.g. `api-server-1.0.0.jar`) should be placed in the directory you are running the docker
build command from. The command to build the image is:

`docker build . -t mt-api-server`

To run the server you must supply the required configuration and any additional
dependencies (connector JARs) in a directory that can be mounted inside the
container at runtime (the files will be mounted at `opt/bin/mt-api-server/conf`).

The configuration file format is detailed in the mt-server repo. In this case it
must be named `application.conf`. Any JAR dependencies for connectors must be placed
in the same directory and they will automatically be added to the classpath.

## Joshua Example

To run mt-server in docker using the Joshua connector you must supply the follow
configuration in a file called application.conf and in the same directory place
the Joshua connector JAR.

```
include classpath("application.conf")

mt-server {
	logLevel = INFO
	connector = {
		find = true
		config = {
			server = "http://localhost:5674"
		}
	}
}
```

To run the docker image supply the following command

```
docker run -d -v /directory/containing/conf/and/connector/jar/:/opt/bin/mt-api-server/conf -p 6856:6856 mt-api-server:latest
```


