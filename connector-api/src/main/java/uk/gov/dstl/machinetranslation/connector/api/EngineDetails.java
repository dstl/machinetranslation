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

/** POJO to hold information about the engine */
public final class EngineDetails {
  private final String name;
  private final String version;

  private final boolean supportedLanguagesSupported;
  private final boolean identifyLanguageSupported;
  private final boolean translateSupported;

  /**
   * Constructor for engine supporting all operations
   *
   * @param name Engine name
   * @param version Engine version
   */
  public EngineDetails(String name, String version) {
    this.name = name;
    this.version = version;
    this.supportedLanguagesSupported = true;
    this.identifyLanguageSupported = true;
    this.translateSupported = true;
  }

  /**
   * Constructor for engine supporting some operations
   *
   * @param name Engine name
   * @param version Engine version
   * @param supportedLanguagesSupported Does this engine support the supportedLanguages operation?
   * @param identifyLanguageSupported Does this engine support the identifyLanguage operation?
   * @param translateSupported Does this engine support the translate operation?
   */
  public EngineDetails(
      String name,
      String version,
      boolean supportedLanguagesSupported,
      boolean identifyLanguageSupported,
      boolean translateSupported) {
    this.name = name;
    this.version = version;
    this.supportedLanguagesSupported = supportedLanguagesSupported;
    this.identifyLanguageSupported = identifyLanguageSupported;
    this.translateSupported = translateSupported;
  }

  /** Get name of engine */
  public String getName() {
    return name;
  }

  /** Get version of engine */
  public String getVersion() {
    return version;
  }

  /** Return true if this engine supports the supportedLanguages() operation */
  public boolean isSupportedLanguagesSupported() {
    return supportedLanguagesSupported;
  }

  /** Return true if this engine supports the identifyLanguage(String) operation */
  public boolean isIdentifyLanguageSupported() {
    return identifyLanguageSupported;
  }

  /** Return true if this engine supports the translate(String, String, String) operation */
  public boolean isTranslateSupported() {
    return translateSupported;
  }

  @Override
  public String toString() {
    return "EngineDetails[name = "
        + name
        + ", version = "
        + version
        + ", "
        + "supportedLanguagesSupported = "
        + supportedLanguagesSupported
        + ", "
        + "identifyLanguageSupported = "
        + identifyLanguageSupported
        + ", "
        + "translateSupported = "
        + translateSupported
        + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;

    if (getClass() != obj.getClass()) return false;

    EngineDetails ed = (EngineDetails) obj;

    return Objects.equals(name, ed.getName())
        && Objects.equals(version, ed.getVersion())
        && Objects.equals(supportedLanguagesSupported, ed.isSupportedLanguagesSupported())
        && Objects.equals(identifyLanguageSupported, ed.isIdentifyLanguageSupported())
        && Objects.equals(translateSupported, ed.isTranslateSupported());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        name, version, supportedLanguagesSupported, identifyLanguageSupported, translateSupported);
  }
}
