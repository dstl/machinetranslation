package uk.gov.dstl.machinetranslation.connector.remedi;

/*-
 * #%L
 * MT API Connector for REMEDI
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import uk.gov.dstl.machinetranslation.connector.api.*;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConfigurationUtils;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;
import uk.gov.nca.remedi4j.client.RemediClient;
import uk.gov.nca.remedi4j.data.PostProcessorResponse;
import uk.gov.nca.remedi4j.data.PreProcessorResponse;
import uk.gov.nca.remedi4j.data.StatusCode;
import uk.gov.nca.remedi4j.data.TranslationResponse;

/** Connector for REMEDI (https://github.com/ivan-zapreev/Distributed-Translation-Infrastructure) */
public class RemediConnector implements MTConnectorApi {

  private URI preProcessingServer;
  private URI translationServer;
  private URI postProcessingServer;

  /** Property key for specifying the translation server */
  public static final String PROP_TRANSLATION_SERVER = "translationServer";

  /** Property key for specifying the pre-processing server */
  public static final String PROP_PRE_PROCESSING_SERVER = "preProcessingServer";

  /** Property key for specifying the post-processing server */
  public static final String PROP_POST_PROCESSING_SERVER = "postProcessingServer";

  /** Create new REMEDI connector with default configuration */
  public RemediConnector() {
    try {
      configure(Collections.emptyMap());
    } catch (ConfigurationException ce) {
      // This shouldn't happen...
      throw new RuntimeException("Unable to create default configuration", ce);
    }
  }

  /**
   * Create a REMEDI connector with just a translation server, and no pre- or post-processing
   *
   * @param translationServer URI of the translation server
   */
  public RemediConnector(URI translationServer) throws ConfigurationException {
    Map<String, Object> conf = new HashMap<>();
    conf.put(PROP_TRANSLATION_SERVER, translationServer);

    configure(conf);
  }

  /**
   * Create a REMEDI connector
   *
   * @param preProcessingServer URI of the pre-processing server
   * @param translationServer URI of the translation server
   * @param postProcessingServer URI of the post-processing server
   */
  protected RemediConnector(
      URI preProcessingServer, URI translationServer, URI postProcessingServer)
      throws ConfigurationException {
    Map<String, Object> conf = new HashMap<>();
    if (preProcessingServer != null) conf.put(PROP_PRE_PROCESSING_SERVER, preProcessingServer);
    conf.put(PROP_TRANSLATION_SERVER, translationServer);
    if (postProcessingServer != null) conf.put(PROP_POST_PROCESSING_SERVER, postProcessingServer);

    configure(conf);
  }

  /**
   * Configures this instance of RemediConnector. The following keys are accepted:
   *
   * <ul>
   *   <li><strong>{@value PROP_TRANSLATION_SERVER}</strong> - The URI of the translation server
   *       (defaults to ws://localhost:9090)
   *   <li><strong>{@value PROP_PRE_PROCESSING_SERVER}</strong> (optional) - The URI of the
   *       pre-processing server (defaults to null)
   *   <li><strong>{@value PROP_POST_PROCESSING_SERVER}</strong> (optional) - The URI of the
   *       post-processing server (defaults to null)
   * </ul>
   *
   * @param map Key-value pairs
   */
  @Override
  public void configure(Map<String, Object> map) throws ConfigurationException {
    // Translation server
    if (map.containsKey(PROP_TRANSLATION_SERVER)) {
      this.translationServer = ConfigurationUtils.getURI(map.get(PROP_TRANSLATION_SERVER));
    } else {
      try {
        this.translationServer = new URI("ws://localhost:9090");
      } catch (URISyntaxException e) {
        throw new ConfigurationException("Couldn't set default URI for translation server", e);
      }
    }

    // Pre-processing server
    if (map.containsKey(PROP_PRE_PROCESSING_SERVER)) {
      this.preProcessingServer = ConfigurationUtils.getURI(map.get(PROP_PRE_PROCESSING_SERVER));
    } else {
      this.preProcessingServer = null;
    }

    // Post-processing server
    if (map.containsKey(PROP_POST_PROCESSING_SERVER)) {
      this.postProcessingServer = ConfigurationUtils.getURI(map.get(PROP_POST_PROCESSING_SERVER));
    } else {
      this.postProcessingServer = null;
    }
  }

  @Override
  public Collection<LanguagePair> supportedLanguages() throws ConnectorException {
    try (RemediClient client =
        new RemediClient(preProcessingServer, translationServer, postProcessingServer)) {
      return supportedLanguages(client);
    }
  }

  /** Get supported languages, with a specified client (for testing purposes) */
  protected Collection<LanguagePair> supportedLanguages(RemediClient client)
      throws ConnectorException {
    try {
      Collection<LanguagePair> supportedLanguages = new HashSet<>();

      client
          .getSupportedLanguages()
          .get()
          .forEach(
              (key, value) ->
                  value.forEach(
                      f -> {
                        supportedLanguages.add(new LanguagePair(key, f));
                      }));

      return supportedLanguages;
    } catch (InterruptedException | ExecutionException e) {
      throw new ConnectorException("Unable to retrieve supported languages", e);
    }
  }

  @Override
  public List<LanguageDetection> identifyLanguage(String content) {
    throw new UnsupportedOperationException(
        "Language detection is not supported by REMEDI as a standalone capability");
  }

  @Override
  public Translation translate(String sourceLanguage, String targetLanguage, String content)
      throws ConnectorException {
    try (RemediClient client =
        new RemediClient(preProcessingServer, translationServer, postProcessingServer)) {
      return translate(client, sourceLanguage, targetLanguage, content);
    }
  }

  /** Translate text, with a specified client (for testing purposes) */
  protected Translation translate(
      RemediClient client, String sourceLanguage, String targetLanguage, String content)
      throws ConnectorException {
    try {
      boolean languageAuto = ConnectorUtils.LANGUAGE_AUTO.equalsIgnoreCase(sourceLanguage);

      if (!languageAuto
          && !supportedLanguages(client).contains(new LanguagePair(sourceLanguage, targetLanguage)))
        throw new ConnectorException(
            "Requested translation is between languages not supported by REMEDI");

      // TODO: Support for chunking?

      String processed;
      String srcLanguage; // Separate variable to capture the source language if detected

      // If a pre-processor has been configured, then pre-process
      if (preProcessingServer != null) {
        PreProcessorResponse response = client.preProcess(sourceLanguage, content).get();

        if (response.getStatusCode() != StatusCode.RESULT_OK)
          throw new ConnectorException(
              "REMEDI returned a non-OK error code ("
                  + response.getStatusCode().name()
                  + ") during pre-processing");

        processed = response.getText();
        srcLanguage = response.getLanguage();
      } else {
        // Otherwise, set internal variables to be equal to those passed in

        if (languageAuto) {
          throw new UnsupportedOperationException(
              "Language detection is performed by the pre-processor server in REMEDI, which hasn't been configured");
        }

        processed = content;
        srcLanguage = sourceLanguage;
      }

      // Perform translation - we always do this
      TranslationResponse translationResponse =
          client.translate(srcLanguage, targetLanguage, processed).get();

      if (translationResponse.getStatusCode() != StatusCode.RESULT_OK)
        throw new ConnectorException(
            "REMEDI returned a non-OK error code ("
                + translationResponse.getStatusCode().name()
                + ") during translation");

      processed = translationResponse.assembleTargetData(" ", true);

      // If a post-processor has been configured, then post-process
      if (postProcessingServer != null) {
        PostProcessorResponse response = client.postProcess(targetLanguage, processed).get();

        if (response.getStatusCode() != StatusCode.RESULT_OK)
          throw new ConnectorException(
              "REMEDI returned a non-OK error code ("
                  + response.getStatusCode().name()
                  + ") during post-processing");

        processed = response.getText();
      }

      // Return the translation
      return new Translation(srcLanguage, processed);

    } catch (InterruptedException | ExecutionException e) {
      throw new ConnectorException("Translation failed", e);
    }
  }

  @Override
  public EngineDetails queryEngine() {
    return new EngineDetails("REMEDI", ConnectorUtils.VERSION_UNKNOWN, true, false, true);
  }
}
