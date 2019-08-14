package uk.gov.dstl.machinetranslation.connector.systran;

/*-
 * #%L
 * MT API Connector for SYSTRAN
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

import net.systran.platform.translation.client.ApiClient;
import net.systran.platform.translation.client.ApiException;
import net.systran.platform.translation.client.api.TranslationApi;
import net.systran.platform.translation.client.auth.ApiKeyAuth;
import net.systran.platform.translation.client.model.SupportedLanguageResponse;
import net.systran.platform.translation.client.model.TranslationOutput;
import net.systran.platform.translation.client.model.TranslationResponse;
import uk.gov.dstl.machinetranslation.connector.api.*;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Connector for SYSTRAN */
public class SystranConnector implements MTConnectorApi {
  private TranslationApi api;

  /** Property name for the SYSTRAN API key */
  public static final String PROP_API_KEY = "apiKey";

  /** Property name for the SYSTRAN base path */
  public static final String PROP_BASE_PATH = "basePath";

  /**
   * Create new connector with default API Client (which can be configured via {@link
   * #configure(Map)})
   */
  public SystranConnector() {
    api = new TranslationApi(new ApiClient());
  }

  /** Create new connector with specified API Client, primarily for testing */
  protected SystranConnector(TranslationApi translationApi) {
    api = translationApi;
  }

  @Override
  public void configure(Map<String, Object> configuration) throws ConfigurationException {
    if (configuration.containsKey(PROP_API_KEY)) {
      ApiKeyAuth apiKeyAuth = (ApiKeyAuth) api.getApiClient().getAuthentication("apiKey");
      apiKeyAuth.setApiKey(configuration.get(PROP_API_KEY).toString());
    }

    if (configuration.containsKey(PROP_BASE_PATH)) {
      api.getApiClient().setBasePath(configuration.get(PROP_BASE_PATH).toString());
    }
  }

  @Override
  public Collection<LanguagePair> supportedLanguages() throws ConnectorException {
    SupportedLanguageResponse response;
    try {
      response = api.translationSupportedLanguagesGet(null, null, null);
    } catch (ApiException e) {
      throw new ConnectorException("Could not retrieve supported languages", e);
    }

    return response.getLanguagePairs().stream()
        .map(lp -> new LanguagePair(lp.getSource(), lp.getTarget()))
        .collect(Collectors.toSet());
  }

  @Override
  public List<LanguageDetection> identifyLanguage(String content) {
    throw new UnsupportedOperationException(
        "Language identification is not supported by SYSTRAN as a separate capability");
  }

  @Override
  public Translation translate(String sourceLanguage, String targetLanguage, String content)
      throws ConnectorException {
    TranslationResponse response;
    try {
      response =
          api.translationTextTranslateGet(
              Collections.singletonList(content),
              sourceLanguage,
              targetLanguage,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null);
    } catch (ApiException e) {
      throw new ConnectorException("Could not translate text", e);
    }

    if (response.getError() != null)
      throw new ConnectorException(
          "Translation error occurred: " + response.getError().getMessage());

    if (response.getOutputs().size() != 1)
      throw new ConnectorException(
          "Unexpected number of outputs (" + response.getOutputs().size() + ") from translation");

    TranslationOutput output = response.getOutputs().get(0);

    String src = output.getDetectedLanguage();
    if (src == null) src = sourceLanguage;

    return new Translation(src, output.getOutput());
  }

  @Override
  public EngineDetails queryEngine() {
    return new EngineDetails("SYSTRAN", ConnectorUtils.VERSION_UNKNOWN, true, false, true);
  }
}
