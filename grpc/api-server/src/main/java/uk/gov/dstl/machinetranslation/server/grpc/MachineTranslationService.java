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

import static uk.gov.dstl.machinetranslation.server.utils.ApiToGrpcUtils.*;

import io.grpc.stub.StreamObserver;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dstl.machinetranslation.MachineTranslation;
import uk.gov.dstl.machinetranslation.MachineTranslationServiceGrpc;
import uk.gov.dstl.machinetranslation.connector.api.*;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;

public class MachineTranslationService
    extends MachineTranslationServiceGrpc.MachineTranslationServiceImplBase {

  private static final Logger logger = LoggerFactory.getLogger(MachineTranslationService.class);

  private final MTConnectorApi connector;

  public MachineTranslationService(MTConnectorApi connector) {
    super();
    this.connector = connector;
  }

  @Override
  public void supportedLanguages(
      MachineTranslation.SupportedLanguagesRequest request,
      StreamObserver<MachineTranslation.SupportedLanguagesResponse> responseObserver) {
    logger.info("Received supportedLanguages request, passing to connector");

    try {
      Collection<LanguagePair> supportedLanguages = connector.supportedLanguages();

      responseObserver.onNext(
          MachineTranslation.SupportedLanguagesResponse.newBuilder()
              .addAllLanguages(buildLanguagePairCollection(supportedLanguages))
              .build());
      responseObserver.onCompleted();
    } catch (ConnectorException e) {
      logger.error("Error performing supportedLanguages request", e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void identifyLanguage(
      MachineTranslation.IdentifyLanguageRequest request,
      StreamObserver<MachineTranslation.IdentifyLanguageResponse> responseObserver) {
    logger.info("Received identifyLanguage request, passing to connector");

    try {
      Collection<LanguageDetection> identifiedLanguages =
          connector.identifyLanguage(request.getContent());

      responseObserver.onNext(
          MachineTranslation.IdentifyLanguageResponse.newBuilder()
              .addAllDetections(buildLanguageDetectionCollection(identifiedLanguages))
              .build());
      responseObserver.onCompleted();
    } catch (ConnectorException e) {
      logger.error("Error performing identifyLanguage request", e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void translate(
      MachineTranslation.TranslationRequest request,
      StreamObserver<MachineTranslation.TranslationResponse> responseObserver) {
    logger.info("Received translate request, passing to connector");

    try {
      Translation translation =
          connector.translate(
              request.getSourceLanguage(), request.getTargetLanguage(), request.getContent());

      responseObserver.onNext(buildTranslationResponse((translation)));
      responseObserver.onCompleted();
    } catch (ConnectorException e) {
      logger.error("Error performing translate request", e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void queryEngine(
      MachineTranslation.QueryEngineRequest request,
      StreamObserver<MachineTranslation.QueryEngineResponse> responseObserver) {
    logger.info("Received queryEngine request, passing to connector");

    EngineDetails engineDetails = connector.queryEngine();

    responseObserver.onNext(buildQueryEngineResponse(engineDetails));
    responseObserver.onCompleted();
  }
}
