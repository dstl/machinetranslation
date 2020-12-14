FROM ubuntu:16.04

# Update, patch and install dependencies
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get upgrade -f -y
RUN apt-get install -y git openjdk-8-jdk maven

# Create dirs
RUN mkdir -p /opt/bin/mt-api-server

# Get server jar
WORKDIR /opt/bin/mt-api-server
ADD api-server-*.jar .

VOLUME ["/opt/bin/mt-api-server/conf"]
ENTRYPOINT java -Dconfig.file=./conf/application.conf -cp "./*:./conf/*" uk.gov.dstl.machinetranslation.server.MtServerApplication
EXPOSE 6856
