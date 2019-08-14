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

import java.util.Objects;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

/**
 * Holds the results of a translation and the language of the source text.
 *
 * <p>The source language is included in the response as this may have been detected as part of the
 * translation process if {@link ConnectorUtils#LANGUAGE_AUTO} is passed as the original source
 * language.
 */
public final class Translation {
  private final String sourceLanguage;
  private final String content;

  /**
   * Constructor
   *
   * @param sourceLanguage Language of the original text (if this has been automatically detected,
   *     the actual language should be returned here)
   * @param content Content of the translated text
   */
  public Translation(String sourceLanguage, String content) {
    this.sourceLanguage = sourceLanguage;
    this.content = content;
  }

  /** Get source language */
  public String getSourceLanguage() {
    return sourceLanguage;
  }

  /** Get translated content */
  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return "Translation[sourceLanguage = " + sourceLanguage + ", content = " + content + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;

    if (getClass() != obj.getClass()) return false;

    Translation t = (Translation) obj;

    return Objects.equals(sourceLanguage, t.getSourceLanguage())
        && Objects.equals(content, t.getContent());
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceLanguage, content);
  }
}
