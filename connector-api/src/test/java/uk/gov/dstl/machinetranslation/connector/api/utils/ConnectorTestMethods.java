package uk.gov.dstl.machinetranslation.connector.api.utils;

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

import static org.junit.jupiter.api.Assertions.fail;

import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.MTConnectorApi;

public class ConnectorTestMethods {
  public static void testSupportedOperations(MTConnectorApi connector) {
    EngineDetails ed = connector.queryEngine();

    if (ed.isSupportedLanguagesSupported()) {
      // Check that it doesn't throw UnsupportedOperationException
      try {
        connector.supportedLanguages();
      } catch (Exception e) {
        if (e instanceof UnsupportedOperationException)
          fail(
              "Connector claims supportedLanguage is supported, but UnsupportedOperationException was thrown");
      }
    } else {
      // Check that it throws UnsupportedOperationException
      try {
        connector.supportedLanguages();
        fail(
            "Connector claims supportedLanguage is not supported, but UnsupportedOperationException wasn't thrown");
      } catch (UnsupportedOperationException e) {
        // Do nothing, this is expected
      } catch (Exception e) {
        fail(
            "Connector claims supportedLanguage is not supported, but UnsupportedOperationException wasn't thrown");
      }
    }

    if (ed.isIdentifyLanguageSupported()) {
      // Check that it doesn't throw UnsupportedOperationException
      try {
        connector.identifyLanguage("Bonjour le monde");
      } catch (Exception e) {
        if (e instanceof UnsupportedOperationException)
          fail(
              "Connector claims identifyLanguage is supported, but UnsupportedOperationException was thrown");
      }
    } else {
      // Check that it throws UnsupportedOperationException
      try {
        connector.identifyLanguage("Bonjour le monde");
        fail(
            "Connector claims identifyLanguage is not supported, but UnsupportedOperationException wasn't thrown");
      } catch (UnsupportedOperationException e) {
        // Do nothing, this is expected
      } catch (Exception e) {
        fail(
            "Connector claims identifyLanguage is not supported, but UnsupportedOperationException wasn't thrown");
      }
    }

    if (ed.isTranslateSupported()) {
      // Check that it doesn't throw UnsupportedOperationException
      try {
        connector.translate("fr", "en", "Bonjour le monde");
      } catch (Exception e) {
        if (e instanceof UnsupportedOperationException)
          fail(
              "Connector claims translate is supported, but UnsupportedOperationException was thrown");
      }
    } else {
      // Check that it throws UnsupportedOperationException
      try {
        connector.translate("fr", "en", "Bonjour le monde");
        fail(
            "Connector claims translate is not supported, but UnsupportedOperationException wasn't thrown");
      } catch (UnsupportedOperationException e) {
        // Do nothing, this is expected
      } catch (Exception e) {
        fail(
            "Connector claims translate is not supported, but UnsupportedOperationException wasn't thrown");
      }
    }
  }
}
