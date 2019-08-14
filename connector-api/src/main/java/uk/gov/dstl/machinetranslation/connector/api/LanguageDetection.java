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

/** POJO to hold a detected language and it's associated probability */
public final class LanguageDetection {
  private final double probability;
  private final String language;

  /**
   * Constructor
   *
   * @param probability Probability of this detection
   * @param language Detected language
   */
  public LanguageDetection(double probability, String language) {
    this.probability = probability;
    this.language = language;
  }

  /** Get probability (should be in range 0.0 to 1.0 inclusive) */
  public double getProbability() {
    return probability;
  }

  /** Get detected language */
  public String getLanguage() {
    return language;
  }

  @Override
  public String toString() {
    return "LanguageDetection[probability = " + probability + ", language = " + language + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;

    if (getClass() != obj.getClass()) return false;

    LanguageDetection ld = (LanguageDetection) obj;

    return Objects.equals(probability, ld.getProbability())
        && Objects.equals(language, ld.getLanguage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(probability, language);
  }
}
