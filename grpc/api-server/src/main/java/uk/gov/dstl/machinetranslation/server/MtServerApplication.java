package uk.gov.dstl.machinetranslation.server;

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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dstl.machinetranslation.connector.api.MTConnectorApi;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.server.grpc.MachineTranslationGrpcServer;
import uk.gov.dstl.machinetranslation.server.grpc.MachineTranslationService;

public class MtServerApplication {

  private static Logger logger = LoggerFactory.getLogger(MtServerApplication.class);

  public static void main(String[] args) {

    try {

      /*
       * Load config (note, log output from this command may not be surpressed by the logLevel config element
       * as this cannot be set before config loading)
       */
      Config conf = ConfigFactory.load();

      /*
       * Configure log level
       */
      LogManager.getRootLogger().setLevel(Level.toLevel(conf.getString("mt-server.logLevel")));

      logger.debug(conf.toString());

      MTConnectorApi mtConnectorApi = null;

      /*
       * Find something that implements the connector or load the class specified in config
       */
      if (conf.getBoolean("mt-server.connector.find")) {
        logger.info("Attempting to find connector on classpath");

        ServiceLoader<MTConnectorApi> loader = ServiceLoader.load(MTConnectorApi.class);

        Iterator<MTConnectorApi> iter = loader.iterator();
        if (iter.hasNext()) {
          mtConnectorApi = iter.next();
          logger.info("Found connector {}", mtConnectorApi.getClass().getName());
        } else {
          logger.error("Couldn't find connector");
          System.exit(-1);
        }

      } else {
        logger.info("Attempting to load class specified in config");

        final String specifiedClass = conf.getString("mt-server.connector.class");
        logger.info("Specified connector: {}", specifiedClass);

        Class<?> clazz = Class.forName(specifiedClass);
        mtConnectorApi = (MTConnectorApi) clazz.getConstructor().newInstance();
      }

      logger.info(
          "Loaded connector {} (engine version {})",
          mtConnectorApi.queryEngine().getName(),
          mtConnectorApi.queryEngine().getVersion());

      Map<String, Object> connectorConfig =
          configToMap(conf.getConfig("mt-server.connector.config").entrySet());
      mtConnectorApi.configure(connectorConfig);
      logger.info("Connector configured");

      if (conf.getBoolean("mt-server.grpc.enabled")) {

        /*
         * Create GRPC server
         */
        logger.info("gRPC Server Enabled");

        MachineTranslationService grpcService = new MachineTranslationService(mtConnectorApi);

        MachineTranslationGrpcServer grpcServer =
            new MachineTranslationGrpcServer(conf.getInt("mt-server.grpc.port"), grpcService);

        grpcServer.start();
        grpcServer.blockUntilShutdown();
      } else {
        logger.warn("gRPC Server Disabled - no other endpoints currently supported");
      }

    } catch (IOException e) {
      logger.error("IOException when starting server", e);
    } catch (InterruptedException e) {
      logger.error("Interrupted when waiting for termination ", e);
    } catch (IllegalAccessException e) {
      logger.error("Unable to access specified connector class ", e);
    } catch (InvocationTargetException | NoSuchMethodException | InstantiationException e) {
      logger.error("Unable to instantiate specified connector class ", e);
    } catch (ClassNotFoundException e) {
      logger.error("Unable to find specified connector class ", e);
    } catch (ConfigurationException e) {
      logger.error("Unable to configure connector ", e);
    }
  }

  private static Map<String, Object> configToMap(Set<Map.Entry<String, ConfigValue>> config) {
    Map<String, Object> result = new HashMap<>();

    for (Map.Entry<String, ConfigValue> entry : config) {
      result.put(entry.getKey(), entry.getValue().unwrapped());
    }

    return result;
  }
}
