package uk.gov.dstl.machinetranslation.connector.api;

/*-
 * #%L
 * Machine Translation Connector API
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

/**
 * Connector interface for Machine Translation
 *
 * <p>Connectors should implement this interface, accepting and providing responses in the specified
 * format. If a particular method is not supported by the engine, then an {@link
 * UnsupportedOperationException} should be thrown.
 *
 * <p>Implementations should provide a no-args constructor, and configuration should be done via the
 * {@link #configure(Map)} method.
 *
 * <p>Implementations should also ensure that they are compatible with the Java ServiceLoader, by
 * providing the following file which should contain the fully qualified name of the connector:
 *
 * <pre>src/main/resources/META-INF/services/MTConnectorApi</pre>
 */
public interface MTConnectorApi {

  /**
   * Configure the connector. Connector should respond to this dynamically (i.e. configure can be
   * called at any time)
   *
   * @param configuration Key-value pairs detailing the configuration
   */
  void configure(Map<String, Object> configuration) throws ConfigurationException;

  /** Query which language pairs are supported by the engine */
  Collection<LanguagePair> supportedLanguages() throws ConnectorException;

  /**
   * Identify the language of some text, returning a sorted list with the most likely language first
   *
   * @param content Content to be translated
   */
  List<LanguageDetection> identifyLanguage(String content) throws ConnectorException;

  /**
   * Translate some text
   *
   * @param sourceLanguage The source language of the text to be translated. If not known, then
   *     {@link ConnectorUtils#LANGUAGE_AUTO} should be used.
   * @param targetLanguage The target language of the translation
   * @param content The text to be translated
   */
  Translation translate(String sourceLanguage, String targetLanguage, String content)
      throws ConnectorException;

  /**
   * Query for information about the engine
   *
   * <p>Should always return
   */
  EngineDetails queryEngine();
}
