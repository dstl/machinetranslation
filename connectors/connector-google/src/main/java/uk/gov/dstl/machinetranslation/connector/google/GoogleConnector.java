package uk.gov.dstl.machinetranslation.connector.google;

/*-
 * #%L
 * MT API Connector for Google Cloud Translate
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

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import java.util.*;
import uk.gov.dstl.machinetranslation.connector.api.*;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

/** Connector to connect to Google Cloud Translate */
public class GoogleConnector implements MTConnectorApi {

  private Translate translateClient;

  /** Create new GoogleConnector with default client */
  public GoogleConnector() {
    translateClient = TranslateOptions.getDefaultInstance().getService();
  }

  /** Create new GoogleConnector with a specified client, primarily for testing */
  protected GoogleConnector(Translate client) {
    translateClient = client;
  }

  /**
   * Configuration is not required for GoogleConnector, and this method is empty.
   *
   * <p>However, before using this connector you will need to set up your machine to authenticate
   * correctly with Google Cloud. This must be done before the connector is instantiated.
   *
   * <p>Instructions on how to do this can be found at
   * https://cloud.google.com/translate/docs/reference/libraries#setting_up_authentication
   *
   * @param configuration This parameter is ignored
   */
  @Override
  public void configure(Map<String, Object> configuration) {
    // Do nothing
  }

  @Override
  public Collection<LanguagePair> supportedLanguages() {
    List<Language> languages = translateClient.listSupportedLanguages();

    List<LanguagePair> languagePairs = new ArrayList<>();
    for (int i = 0; i < languages.size(); i++) {
      for (int j = 0; j < languages.size(); j++) {
        if (i == j) continue;

        languagePairs.add(new LanguagePair(languages.get(i).getCode(), languages.get(j).getCode()));
      }
    }

    return languagePairs;
  }

  @Override
  public List<LanguageDetection> identifyLanguage(String content) {
    Detection d = translateClient.detect(content);

    return Collections.singletonList(new LanguageDetection(d.getConfidence(), d.getLanguage()));
  }

  @Override
  public Translation translate(String sourceLanguage, String targetLanguage, String content) {

    com.google.cloud.translate.Translation translation;
    if (ConnectorUtils.LANGUAGE_AUTO.equals(sourceLanguage)) {
      translation =
          translateClient.translate(
              content, Translate.TranslateOption.targetLanguage(targetLanguage));
    } else {
      translation =
          translateClient.translate(
              content,
              Translate.TranslateOption.sourceLanguage(sourceLanguage),
              Translate.TranslateOption.targetLanguage(targetLanguage));
    }

    return new Translation(translation.getSourceLanguage(), translation.getTranslatedText());
  }

  @Override
  public EngineDetails queryEngine() {
    return new EngineDetails("Google Cloud Translate", ConnectorUtils.VERSION_UNKNOWN);
  }
}
