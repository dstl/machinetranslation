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

/** POJO holding a pair of languages, with a direction (i.e. source to target) */
public final class LanguagePair {
  private final String sourceLanguage;
  private final String targetLanguage;

  /**
   * Constructor
   *
   * @param sourceLanguage Source language of this pair
   * @param targetLanguage Target language of this pair
   */
  public LanguagePair(String sourceLanguage, String targetLanguage) {
    this.sourceLanguage = sourceLanguage;
    this.targetLanguage = targetLanguage;
  }

  /** Get source language */
  public String getSourceLanguage() {
    return sourceLanguage;
  }

  /** Get target language */
  public String getTargetLanguage() {
    return targetLanguage;
  }

  @Override
  public String toString() {
    return "LanguagePair[sourceLanguage = "
        + sourceLanguage
        + ", targetLanguage = "
        + targetLanguage
        + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;

    if (getClass() != obj.getClass()) return false;

    LanguagePair lp = (LanguagePair) obj;

    return Objects.equals(sourceLanguage, lp.getSourceLanguage())
        && Objects.equals(targetLanguage, lp.getTargetLanguage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceLanguage, targetLanguage);
  }
}
