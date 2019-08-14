package uk.gov.dstl.machinetranslation.connector.api.noop;

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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import uk.gov.dstl.machinetranslation.connector.api.*;

/** NoOp connector, that can be used for testing and placeholder implementations. */
public final class NoOpConnector implements MTConnectorApi {

  @Override
  public void configure(Map<String, Object> configuration) {
    // Do nothing, no configuration required
  }

  @Override
  public Collection<LanguagePair> supportedLanguages() {
    return Collections.singletonList(new LanguagePair("en", "en"));
  }

  @Override
  public List<LanguageDetection> identifyLanguage(String content) {
    return Collections.singletonList(new LanguageDetection(1.0, "x-unknown"));
  }

  @Override
  public Translation translate(String sourceLanguage, String targetLanguage, String content) {
    return new Translation(sourceLanguage, content);
  }

  @Override
  public EngineDetails queryEngine() {
    return new EngineDetails("NoOp Engine", "1.0");
  }
}
