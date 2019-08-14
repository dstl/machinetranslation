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

import static org.junit.Assert.*;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.dstl.machinetranslation.MachineTranslation;
import uk.gov.dstl.machinetranslation.MachineTranslationServiceGrpc;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.noop.NoOpConnector;
import uk.gov.dstl.machinetranslation.server.grpc.MachineTranslationService;

public class MtServerApplicationTest {

  /** Rule ensures automatic graceful shutdown of gRPC server and channels */
  @Rule public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  @Test
  public void mtServiceImpl_translateMessage() throws Exception {

    String serverName = InProcessServerBuilder.generateName();

    MachineTranslationService mtService = new MachineTranslationService(new NoOpConnector());

    grpcCleanupRule.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(mtService)
            .build()
            .start());

    MachineTranslationServiceGrpc.MachineTranslationServiceBlockingStub blockingStub =
        MachineTranslationServiceGrpc.newBlockingStub(
            grpcCleanupRule.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    MachineTranslation.TranslationRequest request =
        MachineTranslation.TranslationRequest.newBuilder()
            .setContent("test")
            .setSourceLanguage("en-gb")
            .setTargetLanguage("en-gb")
            .build();

    MachineTranslation.TranslationResponse reply = blockingStub.translate(request);

    assertEquals("test", reply.getContent());
  }

  @Test
  public void mtServiceImpl_identifyLanguageMessage() throws Exception {

    String serverName = InProcessServerBuilder.generateName();

    MachineTranslationService mtService = new MachineTranslationService(new NoOpConnector());

    grpcCleanupRule.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(mtService)
            .build()
            .start());

    MachineTranslationServiceGrpc.MachineTranslationServiceBlockingStub blockingStub =
        MachineTranslationServiceGrpc.newBlockingStub(
            grpcCleanupRule.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    MachineTranslation.IdentifyLanguageRequest request =
        MachineTranslation.IdentifyLanguageRequest.newBuilder().setContent("test").build();

    MachineTranslation.IdentifyLanguageResponse reply = blockingStub.identifyLanguage(request);

    assertEquals(
        new NoOpConnector().identifyLanguage("test").get(0).getLanguage(),
        reply.getDetections(0).getLanguage());
    assertEquals(
        new NoOpConnector().identifyLanguage("test").get(0).getProbability(),
        reply.getDetections(0).getProbability(),
        0);
  }

  @Test
  public void mtServiceImpl_supportedLanguagesMessage() throws Exception {

    String serverName = InProcessServerBuilder.generateName();

    MachineTranslationService mtService = new MachineTranslationService(new NoOpConnector());

    grpcCleanupRule.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(mtService)
            .build()
            .start());

    MachineTranslationServiceGrpc.MachineTranslationServiceBlockingStub blockingStub =
        MachineTranslationServiceGrpc.newBlockingStub(
            grpcCleanupRule.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    MachineTranslation.SupportedLanguagesRequest request =
        MachineTranslation.SupportedLanguagesRequest.newBuilder().build();

    MachineTranslation.SupportedLanguagesResponse reply = blockingStub.supportedLanguages(request);

    LanguagePair expectedPair = new NoOpConnector().supportedLanguages().iterator().next();

    assertEquals(expectedPair.getSourceLanguage(), reply.getLanguages(0).getSourceLanguage());
    assertEquals(expectedPair.getTargetLanguage(), reply.getLanguages(0).getTargetLanguage());
  }

  @Test
  public void mtServiceImpl_queryEngineMessage() throws Exception {

    String serverName = InProcessServerBuilder.generateName();

    MachineTranslationService mtService = new MachineTranslationService(new NoOpConnector());

    grpcCleanupRule.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(mtService)
            .build()
            .start());

    MachineTranslationServiceGrpc.MachineTranslationServiceBlockingStub blockingStub =
        MachineTranslationServiceGrpc.newBlockingStub(
            grpcCleanupRule.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    MachineTranslation.QueryEngineRequest request =
        MachineTranslation.QueryEngineRequest.newBuilder().build();

    MachineTranslation.QueryEngineResponse reply = blockingStub.queryEngine(request);

    assertEquals(new NoOpConnector().queryEngine().getName(), reply.getName());
    assertEquals(new NoOpConnector().queryEngine().getVersion(), reply.getVersion());
  }
}
