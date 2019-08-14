package uk.gov.dstl.machinetranslation.server.grpc;

/*-
 * #%L
 * MT Server
 * %%
 * Copyright (C) 2019 Dstl
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MachineTranslationGrpcServer {

  private static final Logger logger = LoggerFactory.getLogger(MachineTranslationGrpcServer.class);

  private final int port;
  private final Server server;

  public MachineTranslationGrpcServer(int port, MachineTranslationService service) {

    this.port = port;
    server = ServerBuilder.forPort(this.port).addService(service).build();
  }

  public void start() throws IOException {

    server.start();
    logger.info("Started server, listening on " + port);

    /*
     * Add hook to shutdown server if JVM is exiting. System.out used as we can't
     * guarantee logging framework won't have shutdown at this point.
     */
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.out.println("JVM shutting down, terminating gRPC server");
                  MachineTranslationGrpcServer.this.stop();
                  System.out.println("gRPC server stopped");
                }));
  }

  public void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }
}
